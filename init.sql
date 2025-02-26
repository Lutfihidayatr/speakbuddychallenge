-- Create sequence for ID generation
CREATE SEQUENCE users_id_seq;
CREATE SEQUENCE phrases_id_seq;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_id_seq'),
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Phrases table
CREATE TABLE IF NOT EXISTS phrases (
    id BIGINT PRIMARY KEY DEFAULT nextval('phrases_id_seq'),
    user_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    filename VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Index for foreign key
CREATE INDEX idx_phrases_user_id ON phrases(user_id);

-- Index for soft delete queries
CREATE INDEX idx_phrases_deleted_at ON phrases(deleted_at);

-- Sample data for users table
INSERT INTO users (id, first_name, email, is_active) VALUES
  (1, 'John Doe', 'john@example.com', true),
  (2, 'Jane Smith', 'jane@example.com', true),
  (3, 'Michael Johnson', 'michael@example.com', true),
  (4, 'Sarah Williams', 'sarah@example.com', true),
  (5, 'Robert Brown', 'robert@example.com', true)
ON CONFLICT (id) DO NOTHING;
