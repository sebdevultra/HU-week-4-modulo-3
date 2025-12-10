package com.riwi.creditapplication.infrastructure.adapter.risk;

import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.domain.model.RiskEvaluation;
import com.riwi.creditapplication.domain.port.AffiliateRepositoryPort;
import com.riwi.creditapplication.domain.port.RiskEvaluationPort;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Primary
public class RiskCentralRestAdapter implements RiskEvaluationPort {

    private final RestTemplate restTemplate;
    private final AffiliateRepositoryPort affiliateRepositoryPort;

    // Default to localhost:8082 if not configured
    private final String serviceUrl = "http://localhost:8082/risk-evaluation";

    public RiskCentralRestAdapter(RestTemplate restTemplate, AffiliateRepositoryPort affiliateRepositoryPort) {
        this.restTemplate = restTemplate;
        this.affiliateRepositoryPort = affiliateRepositoryPort;
    }

    @Override
    public RiskEvaluation evaluateRisk(CreditApplication creditApplication) {
        // Fetch affiliate to get document number
        Affiliate affiliate = affiliateRepositoryPort.findById(creditApplication.getAffiliateId())
                .orElseThrow(() -> new IllegalStateException("Affiliate not found"));

        String documentNumber = affiliate.getDocumentNumber();

        try {
            RiskRequest request = new RiskRequest();
            request.setDocumentNumber(documentNumber);

            ResponseEntity<RiskResponse> response = restTemplate.postForEntity(
                    serviceUrl,
                    request,
                    RiskResponse.class);

            if (response.getBody() == null) {
                return createFallbackEvaluation(creditApplication.getId());
            }

            RiskEvaluation evaluation = new RiskEvaluation();
            evaluation.setCreditApplicationId(creditApplication.getId());
            evaluation.setCreditScore(response.getBody().getScore());
            evaluation.setApproved(response.getBody().isApproved());
            evaluation.setEvaluationDate(LocalDateTime.now());

            // Map boolean/score to RiskLevel (simplistic logic)
            int score = response.getBody().getScore();
            if (score >= 700) {
                evaluation.setRiskLevel(RiskEvaluation.RiskLevel.LOW);
            } else if (score >= 500) {
                evaluation.setRiskLevel(RiskEvaluation.RiskLevel.MEDIUM);
            } else {
                evaluation.setRiskLevel(RiskEvaluation.RiskLevel.HIGH);
            }

            return evaluation;

        } catch (Exception e) {
            // Log error and return fallback
            System.err.println("Error calling Risk Central: " + e.getMessage());
            return createFallbackEvaluation(creditApplication.getId());
        }
    }

    @Override
    public RiskEvaluation getRiskEvaluation(Long creditApplicationId) {
        // Mock implementation or throw unsupported if strictly external
        return null;
    }

    @Override
    public boolean hasRiskEvaluation(Long creditApplicationId) {
        // Mock implementation
        return false;
    }

    private RiskEvaluation createFallbackEvaluation(Long appId) {
        RiskEvaluation fallback = new RiskEvaluation();
        fallback.setCreditApplicationId(appId);
        fallback.setCreditScore(0);
        fallback.setRiskLevel(RiskEvaluation.RiskLevel.HIGH); // Fail safe
        fallback.setApproved(false);
        fallback.setEvaluationDate(LocalDateTime.now());
        fallback.setRejectionReason("Risk Service Unavailable");
        return fallback;
    }

    // Inner DTOs to match external service
    private static class RiskRequest {
        private String documentNumber;

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }
    }

    private static class RiskResponse {
        private int score;
        private boolean approved;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public boolean isApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }
    }
}
