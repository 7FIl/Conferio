-- =====================================================
-- Conference Management System - PRODUCTION Init Script
-- =====================================================
-- 
-- THIS SCRIPT IS FOR PRODUCTION DEPLOYMENT ONLY
-- No default users or sample data included
-- 
-- IMPORTANT: You must manually create an admin user after deployment
-- See SECURITY_CHECKLIST.md for detailed instructions
--
-- =====================================================

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'PARTICIPANT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create proposals table
CREATE TABLE IF NOT EXISTS proposals (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    speaker_id BIGINT NOT NULL REFERENCES users(id),
    category VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create sessions table
CREATE TABLE IF NOT EXISTS sessions (
    id BIGSERIAL PRIMARY KEY,
    proposal_id BIGINT NOT NULL UNIQUE REFERENCES proposals(id),
    speaker_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    session_time TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 60,
    room VARCHAR(100) NOT NULL,
    max_participants INTEGER DEFAULT 100,
    current_participants INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create registrations table
CREATE TABLE IF NOT EXISTS registrations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    session_id BIGINT NOT NULL REFERENCES sessions(id),
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, session_id)
);

-- Create feedbacks table
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    proposal_id BIGINT NOT NULL REFERENCES proposals(id),
    feedback_text TEXT NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_proposals_speaker_id ON proposals(speaker_id);
CREATE INDEX IF NOT EXISTS idx_proposals_status ON proposals(status);
CREATE INDEX IF NOT EXISTS idx_sessions_speaker_id ON sessions(speaker_id);
CREATE INDEX IF NOT EXISTS idx_sessions_proposal_id ON sessions(proposal_id);
CREATE INDEX IF NOT EXISTS idx_sessions_status ON sessions(status);
CREATE INDEX IF NOT EXISTS idx_sessions_session_time ON sessions(session_time);
CREATE INDEX IF NOT EXISTS idx_registrations_user_id ON registrations(user_id);
CREATE INDEX IF NOT EXISTS idx_registrations_session_id ON registrations(session_id);
CREATE INDEX IF NOT EXISTS idx_feedbacks_user_id ON feedbacks(user_id);
CREATE INDEX IF NOT EXISTS idx_feedbacks_proposal_id ON feedbacks(proposal_id);

-- =====================================================
-- NO DEFAULT USERS ARE CREATED IN THIS SCRIPT
-- 
-- To create the first admin user, connect to the database
-- and execute the following steps:
--
-- 1. Connect to PostgreSQL as the application user
-- 2. Run the following SQL commands:
--
--    INSERT INTO users (username, email, password, first_name, last_name, role, is_active)
--    VALUES ('admin', 'admin@example.com', '[BCRYPT_HASH]', 'Admin', 'User', 'ADMIN', true);
--
-- 3. Generate a BCrypt hash for your password using:
--    - Online: https://www.bcryptcalculator.com/
--    - CLI: echo -n 'your-password' | htpasswd -BinC 10 admin
--    - Java: BCryptPasswordEncoder.encode("your-password")
--
-- =====================================================

COMMIT;
