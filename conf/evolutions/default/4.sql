# --- !Ups
CREATE TABLE clusters (
	id 			SERIAL PRIMARY KEY,
	mbr		 	GEOMETRY 		NOT NULL,
	location 	json 		NOT NULL,
	adress	 	json 		NOT NULL
);

# --- !Downs
DROP TABLE clusters