-- ============================================================================
-- MySQL Database Creation Script
-- Credit Application Service
-- ============================================================================

-- Create database
CREATE DATABASE IF NOT EXISTS credit_application_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE credit_application_db;

-- Show database info
SELECT 
    'Database created successfully!' AS status,
    DATABASE() AS current_database,
    @@character_set_database AS charset,
    @@collation_database AS collation;

-- ============================================================================
-- Optional: Create dedicated user (recommended for production)
-- ============================================================================

-- Uncomment the following lines to create a dedicated user:
-- CREATE USER IF NOT EXISTS 'credit_user'@'localhost' IDENTIFIED BY 'changeme';
-- GRANT ALL PRIVILEGES ON credit_application_db.* TO 'credit_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Show grants for current user
-- SHOW GRANTS FOR CURRENT_USER();
