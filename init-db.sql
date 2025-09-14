-- Lunar Event Management Database Initialization
-- This script creates the database and user for local development

-- Create database (if not exists)
-- Note: This will be run manually or by your cloud provider

-- Create user and grant permissions
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'lunar_user') THEN
        CREATE USER lunar_user WITH PASSWORD 'lunar_password';
    END IF;
END
$$;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE lunar_events TO lunar_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO lunar_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO lunar_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO lunar_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO lunar_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO lunar_user;
