# --- !Ups
CREATE TABLE users (
	id 			SERIAL 			PRIMARY KEY,
	username 	VARCHAR(100)	UNIQUE NOT NULL
);

# --- !Downs
DROP TABLE users