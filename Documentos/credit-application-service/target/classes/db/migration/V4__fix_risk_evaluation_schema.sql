-- ============================================================================
-- Flyway Migration V4: Fix Risk Evaluation Schema
-- Description: Adds missing columns to risk_evaluations table to match JPA Entity
-- ============================================================================

ALTER TABLE risk_evaluations 
ADD COLUMN external_bureau_score INT,
ADD COLUMN payment_history_score INT;
