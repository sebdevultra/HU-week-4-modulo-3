-- ============================================================================
-- Flyway Migration V1: Initial Schema
-- Description: Creates base tables for Credit Application Service
-- Database: MySQL
-- Author: Credit Application Team
-- Date: 2025-12-09
-- ============================================================================

-- ============================================================================
-- Table: affiliates
-- Description: Stores affiliate information
-- ============================================================================
CREATE TABLE affiliates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    salary DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_affiliate_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_affiliate_salary CHECK (salary > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores affiliate information';

-- ============================================================================
-- Table: credit_applications
-- Description: Stores credit application requests
-- ============================================================================
CREATE TABLE credit_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    term_months INT NOT NULL,
    purpose VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    application_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evaluation_date DATETIME NULL,
    evaluation_comments TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_credit_app_amount CHECK (requested_amount > 0),
    CONSTRAINT chk_credit_app_term CHECK (term_months > 0),
    CONSTRAINT chk_credit_app_status CHECK (status IN ('PENDING', 'UNDER_EVALUATION', 'APPROVED', 'REJECTED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores credit application requests';

-- ============================================================================
-- Table: risk_evaluations
-- Description: Stores risk evaluation results for credit applications
-- ============================================================================
CREATE TABLE risk_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    credit_application_id BIGINT NOT NULL,
    credit_score INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    approved_amount DECIMAL(15, 2),
    interest_rate DECIMAL(5, 4),
    rejection_reason VARCHAR(500),
    evaluation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    debt_to_income_ratio DECIMAL(5, 4),
    payment_capacity_score INT,
    employment_stability_score INT,
    credit_history_score INT,
    recommended_term_months INT,
    monthly_payment DECIMAL(15, 2),
    total_interest DECIMAL(15, 2),
    total_amount DECIMAL(15, 2),
    evaluator_notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_risk_eval_score CHECK (credit_score >= 0 AND credit_score <= 1000),
    CONSTRAINT chk_risk_level CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores risk evaluation results for credit applications';

-- ============================================================================
-- Indexes for affiliates
-- ============================================================================
CREATE UNIQUE INDEX idx_affiliate_document ON affiliates(document_number);
CREATE INDEX idx_affiliate_email ON affiliates(email);
CREATE INDEX idx_affiliate_status ON affiliates(status);
CREATE INDEX idx_affiliate_registration_date ON affiliates(registration_date);
CREATE INDEX idx_affiliate_salary ON affiliates(salary);

-- ============================================================================
-- Indexes for credit_applications
-- ============================================================================
CREATE INDEX idx_credit_app_affiliate ON credit_applications(affiliate_id);
CREATE INDEX idx_credit_app_status ON credit_applications(status);
CREATE INDEX idx_credit_app_date ON credit_applications(application_date);
CREATE INDEX idx_credit_app_amount ON credit_applications(requested_amount);

-- ============================================================================
-- Indexes for risk_evaluations
-- ============================================================================
CREATE UNIQUE INDEX idx_risk_eval_credit_app ON risk_evaluations(credit_application_id);
CREATE INDEX idx_risk_eval_approved ON risk_evaluations(approved);
CREATE INDEX idx_risk_eval_risk_level ON risk_evaluations(risk_level);
CREATE INDEX idx_risk_eval_score ON risk_evaluations(credit_score);
