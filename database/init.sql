-- ============================================
-- Conference Management System Database Setup
-- ============================================

-- Create Database
CREATE DATABASE conference_db;

-- Connect to database
\c conference_db;

-- Create Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Proposals Table
CREATE TABLE proposals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT,
    rejection_reason TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Create Sessions Table
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    proposal_id BIGINT NOT NULL,
    speaker_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    session_time TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 60,
    room VARCHAR(255) NOT NULL,
    max_participants INTEGER DEFAULT 100,
    current_participants INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (proposal_id) REFERENCES proposals(id) ON DELETE CASCADE,
    FOREIGN KEY (speaker_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Registrations Table
CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_session UNIQUE (user_id, session_id)
);

-- Create Feedback Table
CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- Create Indexes for Performance
CREATE INDEX idx_proposals_status ON proposals(status);
CREATE INDEX idx_proposals_user_id ON proposals(user_id);
CREATE INDEX idx_sessions_time ON sessions(session_time);
CREATE INDEX idx_sessions_speaker_id ON sessions(speaker_id);
CREATE INDEX idx_sessions_status ON sessions(status);
CREATE INDEX idx_registrations_user_id ON registrations(user_id);
CREATE INDEX idx_registrations_session_id ON registrations(session_id);
CREATE INDEX idx_feedback_session_id ON feedback(session_id);
CREATE INDEX idx_feedback_user_id ON feedback(user_id);

-- Insert Sample Data (Optional)

-- Sample Users (password: "password123" - hashed with BCrypt)
INSERT INTO users (username, email, password, full_name, role) VALUES
('admin', 'admin@conference.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User', 'ADMIN'),
('coordinator1', 'coordinator@conference.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John Coordinator', 'COORDINATOR'),
('speaker1', 'speaker1@conference.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jane Speaker', 'USER'),
('speaker2', 'speaker2@conference.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob Smith', 'USER'),
('participant1', 'participant1@conference.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice Participant', 'USER');

-- Sample Proposals
INSERT INTO proposals (user_id, title, description, status, submitted_at) VALUES
(3, 'Introduction to Spring Boot', 'A comprehensive guide to building REST APIs with Spring Boot', 'ACCEPTED', CURRENT_TIMESTAMP),
(4, 'Microservices Architecture', 'Best practices for designing microservices', 'ACCEPTED', CURRENT_TIMESTAMP),
(3, 'Advanced Java Concurrency', 'Deep dive into Java concurrent programming', 'PENDING', CURRENT_TIMESTAMP);

-- Sample Sessions
INSERT INTO sessions (proposal_id, speaker_id, title, description, session_time, duration_minutes, room, max_participants, status) VALUES
(1, 3, 'Introduction to Spring Boot', 'A comprehensive guide to building REST APIs with Spring Boot', CURRENT_TIMESTAMP + INTERVAL '7 days', 90, 'Room A', 50, 'SCHEDULED'),
(2, 4, 'Microservices Architecture', 'Best practices for designing microservices', CURRENT_TIMESTAMP + INTERVAL '8 days', 120, 'Room B', 100, 'SCHEDULED');

-- Sample Registrations
INSERT INTO registrations (user_id, session_id, status) VALUES
(5, 1, 'CONFIRMED'),
(5, 2, 'CONFIRMED');

-- Update session participant count
UPDATE sessions SET current_participants = 1 WHERE id = 1;
UPDATE sessions SET current_participants = 1 WHERE id = 2;

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Display table info
\dt

-- Success message
SELECT 'Database setup completed successfully!' AS status;
