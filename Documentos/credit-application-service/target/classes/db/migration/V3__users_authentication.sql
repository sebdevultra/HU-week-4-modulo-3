-- ============================================================================
-- Flyway Migration V3: Users and Authentication
-- Description: Creates users table and user_roles for authentication
-- Database: MySQL
-- Author: Credit Application Team
-- Date: 2025-12-09
-- ============================================================================

-- ============================================================================
-- Table: users
-- Description: Stores user authentication and authorization information
-- ============================================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    last_login DATETIME NULL,
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_user_email CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stores user authentication and authorization data';

-- ============================================================================
-- Table: user_roles
-- Description: Stores user roles (many-to-many relationship)
-- ============================================================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT chk_user_role CHECK (role IN ('ROLE_ADMIN', 'ROLE_ANALISTA', 'ROLE_AFILIADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Maps users to their roles';

-- ============================================================================
-- Indexes for Users
-- ============================================================================
CREATE UNIQUE INDEX idx_user_username ON users(username);
CREATE UNIQUE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_enabled ON users(enabled);
CREATE INDEX idx_user_last_login ON users(last_login);

-- Index for user_roles
CREATE INDEX idx_user_roles_role ON user_roles(role);

-- ============================================================================
-- Insert default admin user
-- Password: admin123 (BCrypt hash)
-- ============================================================================
INSERT INTO users (username, password, email, enabled) 
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- admin123
    'admin@creditapp.com',
    true
);

INSERT INTO user_roles (user_id, role) 
VALUES (
    (SELECT id FROM users WHERE username = 'admin'),
    'ROLE_ADMIN'
);
