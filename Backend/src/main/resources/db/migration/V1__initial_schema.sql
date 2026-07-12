-- Initial schema for Job Application Tracker
-- Placeholder table to verify Flyway is working
-- Will be dropped in a later migration once real tables are added

CREATE TABLE IF NOT EXISTS app_health_check (
                                                id BIGSERIAL PRIMARY KEY,
                                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_health_check DEFAULT VALUES;