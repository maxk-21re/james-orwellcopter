# --- !Ups
CREATE TABLE geologs (
	id 			SERIAL PRIMARY KEY,
	location 	GEOMETRY 		NOT NULL,
	accuracy 	INT 		NOT NULL,
	timestamp 	TIMESTAMP 	NOT NULL
);

# --- !Downs
DROP TABLE geologs