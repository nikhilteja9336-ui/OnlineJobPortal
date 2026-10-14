-- TiDB Cloud Database Schema for Online Job Portal
-- Note: Do NOT use the system database 'sys' because user table creation is denied on 'sys'.
-- Use 'jobportal' database:

CREATE DATABASE IF NOT EXISTS jobportal;
USE jobportal;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS job (
  id BIGINT NOT NULL AUTO_INCREMENT,
  company VARCHAR(255) NOT NULL,
  description VARCHAR(4000) NOT NULL,
  location VARCHAR(255) NOT NULL,
  posted_at DATE NOT NULL,
  posted_by BIGINT NOT NULL,
  salary VARCHAR(255),
  title VARCHAR(255) NOT NULL,
  type VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS application (
  id BIGINT NOT NULL AUTO_INCREMENT,
  applied_at DATE NOT NULL,
  company VARCHAR(255),
  cover_letter VARCHAR(2000),
  job_id BIGINT NOT NULL,
  job_title VARCHAR(255),
  seeker_email VARCHAR(255),
  seeker_id BIGINT NOT NULL,
  seeker_name VARCHAR(255),
  status VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_app_job (job_id),
  KEY idx_app_seeker (seeker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
