CREATE SCHEMA ssdb;

CREATE TABLE ssdb.user (
    id VARCHAR(36) UNIQUE PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(64) NOT NULL
);

CREATE TABLE ssdb.bucket (
	id VARCHAR(36) UNIQUE PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	fk_user_id VARCHAR(36) NOT NULL REFERENCES ssdb.user(id) ON DELETE CASCADE
);

CREATE TABLE ssdb.file (
	id VARCHAR(36) UNIQUE PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	fk_bucket_id VARCHAR(36) NOT NULL REFERENCES ssdb.bucket(id) ON DELETE CASCADE
);