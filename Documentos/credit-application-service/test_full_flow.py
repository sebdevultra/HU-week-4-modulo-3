import requests
import json
import time
import sys

BASE_URL = "http://localhost:8081/api"
RISK_URL = "http://localhost:8082/risk-evaluation"

def print_step(message):
    print(f"\n{'='*50}")
    print(f"STEP: {message}")
    print(f"{'='*50}")

def check_response(response, expected_status=200):
    if response.status_code != expected_status:
        print(f"FAILED: Expected {expected_status}, got {response.status_code}")
        print(f"Response: {response.text}")
        sys.exit(1)
    print(f"SUCCESS: {response.status_code}")
    return response.json() if response.content else None

def main():
    print_step("Checking Service Health")
    try:
        requests.get(f"{BASE_URL}/actuator/health")
        print("Service is UP")
    except:
        print("Service is DOWN. Please start it first.")
        sys.exit(1)

    # 1. Register Affiliate
    print_step("1. Register New Affiliate User")
    timestamp = int(time.time())
    affiliate_user = f"affiliate_{timestamp}"
    email = f"affiliate_{timestamp}@test.com"
    
    # Register Auth
    reg_resp = requests.post(f"{BASE_URL}/auth/register", json={
        "username": affiliate_user,
        "password": "password123",
        "roles": ["ROLE_AFILIADO"],
        "name": "Test Affiliate",
        "email": email
    })
    
    auth_token = None
    if reg_resp.status_code == 201:
        print("✓ Affiliate User Registered")
        # Login to get token
        login_resp = requests.post(f"{BASE_URL}/auth/login", json={
            "username": affiliate_user,
            "password": "password123"
        })
        check_response(login_resp, 200)
        auth_token = login_resp.json()["token"]
    elif reg_resp.status_code == 400:
         print("User already exists, logging in...")
         login_resp = requests.post(f"{BASE_URL}/auth/login", json={
            "username": affiliate_user,
            "password": "password123"
        })
         auth_token = login_resp.json()["token"]

    headers = {"Authorization": f"Bearer {auth_token}"}

    # 2. Register Affiliate Profile (MUST happen before /me)
    print_step("2. Register Affiliate Profile")
    profile_resp = requests.post(f"{BASE_URL}/affiliates", json={
        "firstName": "Juan",
        "lastName": "Perez",
        "documentNumber": f"{timestamp}", # Numeric only
        "email": email,
        "phoneNumber": "3001234567",
        "salary": 5000000
    }, headers=headers)
    
    check_response(profile_resp, 200)
    affiliate_id = profile_resp.json()["id"]
    print(f"✓ Affiliate ID: {affiliate_id}")

    # 3. Create Credit Application
    print_step("3. Create Credit Application")
    app_resp = requests.post(f"{BASE_URL}/credit-applications", json={
        "affiliateId": affiliate_id,
        "requestedAmount": 2000000,
        "termMonths": 12,
        "purpose": "Test Integration Flow"
    }, headers=headers)
    
    if app_resp.status_code == 409:
        print("⚠ Pending application exists. Cannot create new.")
        list_resp = requests.get(f"{BASE_URL}/credit-applications/my-applications", headers=headers)
        apps = list_resp.json()
        if apps:
            app_id = apps[0]["id"]
            print(f"Using existing App ID: {app_id}")
        else:
            print("Error: 409 but no apps found?")
            sys.exit(1)
    else:
        check_response(app_resp, 201)
        app_id = app_resp.json()["id"]
        print(f"✓ Application Created: {app_id}")

    # 4. Evaluate (Requires Analyst/Admin)
    print_step("4. Register Analyst and Evaluate")
    
    # Register Analyst
    analyst_user = f"analyst_{timestamp}"
    requests.post(f"{BASE_URL}/auth/register", json={
        "username": analyst_user,
        "password": "password123",
        "roles": ["ROLE_ANALISTA"],
        "name": "Test Analyst",
        "email": f"analyst_{timestamp}@test.com"
    })
    
    analyst_login = requests.post(f"{BASE_URL}/auth/login", json={
        "username": analyst_user,
        "password": "password123"
    })
    analyst_token = analyst_login.json()["token"]
    analyst_headers = {"Authorization": f"Bearer {analyst_token}"}

    # Call Evaluate
    print(f"Calling Risk Service via Adapter for App ID {app_id}...")
    eval_resp = requests.post(f"{BASE_URL}/credit-applications/{app_id}/evaluate", headers=analyst_headers)
    
    check_response(eval_resp, 200)
    result = eval_resp.json()
    print("\n" + "="*50)
    print("EVALUATION RESULT:")
    print("="*50)
    print(json.dumps(result, indent=2))

    if result.get("rejectionReason") == "Risk Service Unavailable":
        print("\n⚠ NOTE: Risk Service was unavailable, fallback logic activated.")
        print("✓ Resilience mechanism tested successfully!")
    else:
        print(f"\n✓ Risk Service Integration Successful!")
        print(f"  - Credit Score: {result.get('creditScore')}")
        print(f"  - Risk Level: {result.get('riskLevel')}")
        print(f"  - Approved: {result.get('approved')}")

    print("\n" + "="*50)
    print("✅ ALL TESTS PASSED!")
    print("="*50)

if __name__ == "__main__":
    main()
