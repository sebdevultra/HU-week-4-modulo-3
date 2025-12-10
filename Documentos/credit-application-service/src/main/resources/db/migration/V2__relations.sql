-- ============================================================================
-- Flyway Migration V2: Relations and Advanced Indexes
-- Description: Adds foreign keys, composite indexes, and database views
-- Database: MySQL
-- Author: Credit Application Team
-- Date: 2025-12-09
-- ============================================================================

-- ============================================================================
-- Foreign Keys
-- ============================================================================

-- Foreign key from credit_applications to affiliates
ALTER TABLE credit_applications
ADD CONSTRAINT fk_credit_app_affiliate
FOREIGN KEY (affiliate_id) REFERENCES affiliates(id)
ON DELETE RESTRICT
ON UPDATE CASCADE;

-- Foreign key from risk_evaluations to credit_applications
ALTER TABLE risk_evaluations
ADD CONSTRAINT fk_risk_eval_credit_app
FOREIGN KEY (credit_application_id) REFERENCES credit_applications(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- ============================================================================
-- Composite Indexes for Performance
-- ============================================================================

-- Composite index for affiliate status and registration date
CREATE INDEX idx_affiliate_status_date 
ON affiliates(status, registration_date);

-- Composite index for affiliate status and salary
CREATE INDEX idx_affiliate_status_salary 
ON affiliates(status, salary);

-- Composite index for credit applications by affiliate and status
CREATE INDEX idx_credit_app_affiliate_status 
ON credit_applications(affiliate_id, status);

-- Composite index for credit applications by status and date
CREATE INDEX idx_credit_app_status_date 
ON credit_applications(status, application_date);

-- ============================================================================
-- Additional Indexes for Performance
-- Note: MySQL doesn't support partial indexes with WHERE clause like PostgreSQL
-- We use regular indexes instead
-- ============================================================================

-- Index for pending applications (without WHERE clause)
CREATE INDEX idx_credit_app_pending 
ON credit_applications(status, application_date, affiliate_id);

-- Index for active affiliates (without WHERE clause)
CREATE INDEX idx_affiliate_active 
ON affiliates(status, registration_date, salary);


-- ============================================================================
-- Additional Constraints
-- Note: MySQL CHECK constraints cannot contain subqueries
-- Complex validations should be done at application level
-- ============================================================================

-- Ensure approved amount is positive when not null
ALTER TABLE risk_evaluations
ADD CONSTRAINT chk_approved_amount_positive
CHECK (approved_amount IS NULL OR approved_amount > 0);

-- Ensure interest rate is reasonable
ALTER TABLE risk_evaluations
ADD CONSTRAINT chk_interest_rate_valid
CHECK (interest_rate IS NULL OR (interest_rate >= 0.01 AND interest_rate <= 0.50));

-- Ensure debt to income ratio is valid
ALTER TABLE risk_evaluations
ADD CONSTRAINT chk_debt_to_income_valid
CHECK (debt_to_income_ratio IS NULL OR (debt_to_income_ratio >= 0 AND debt_to_income_ratio <= 1));

-- ============================================================================
-- Views for Common Queries
-- ============================================================================

-- View: Affiliate statistics with application counts
CREATE OR REPLACE VIEW v_affiliate_statistics AS
SELECT 
    a.id,
    a.first_name,
    a.last_name,
    a.document_number,
    a.email,
    a.salary,
    a.status,
    a.registration_date,
    COUNT(ca.id) AS total_applications,
    SUM(CASE WHEN ca.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_applications,
    SUM(CASE WHEN ca.status = 'APPROVED' THEN 1 ELSE 0 END) AS approved_applications,
    SUM(CASE WHEN ca.status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected_applications,
    MAX(ca.application_date) AS last_application_date
FROM affiliates a
LEFT JOIN credit_applications ca ON a.id = ca.affiliate_id
GROUP BY a.id, a.first_name, a.last_name, a.document_number, a.email, 
         a.salary, a.status, a.registration_date;

-- View: Credit application details with affiliate and risk evaluation
CREATE OR REPLACE VIEW v_application_details AS
SELECT 
    ca.id AS application_id,
    ca.requested_amount,
    ca.term_months,
    ca.purpose,
    ca.status AS application_status,
    ca.application_date,
    ca.evaluation_date,
    a.id AS affiliate_id,
    a.first_name,
    a.last_name,
    a.document_number,
    a.email,
    a.salary,
    a.status AS affiliate_status,
    re.id AS risk_evaluation_id,
    re.credit_score,
    re.risk_level,
    re.approved,
    re.approved_amount,
    re.interest_rate,
    re.monthly_payment,
    re.rejection_reason
FROM credit_applications ca
INNER JOIN affiliates a ON ca.affiliate_id = a.id
LEFT JOIN risk_evaluations re ON ca.id = re.credit_application_id;

-- View: Risk evaluation summary
CREATE OR REPLACE VIEW v_risk_evaluation_summary AS
SELECT 
    re.id,
    re.credit_application_id,
    ca.affiliate_id,
    CONCAT(a.first_name, ' ', a.last_name) AS affiliate_name,
    ca.requested_amount,
    re.approved_amount,
    re.credit_score,
    re.risk_level,
    re.approved,
    re.interest_rate,
    re.monthly_payment,
    re.debt_to_income_ratio,
    re.evaluation_date,
    CASE 
        WHEN re.approved = TRUE THEN 'APPROVED'
        ELSE 'REJECTED'
    END AS evaluation_result
FROM risk_evaluations re
INNER JOIN credit_applications ca ON re.credit_application_id = ca.id
INNER JOIN affiliates a ON ca.affiliate_id = a.id;

-- ============================================================================
-- Comments for Documentation
-- ============================================================================

-- Affiliates table
ALTER TABLE affiliates COMMENT = 'Stores affiliate personal and financial information';

-- Credit Applications table
ALTER TABLE credit_applications COMMENT = 'Stores credit application requests from affiliates';

-- Risk Evaluations table
ALTER TABLE risk_evaluations COMMENT = 'Stores risk analysis results for credit applications';
