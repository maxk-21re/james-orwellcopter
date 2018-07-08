# --- !Ups
ALTER TABLE geologs 
	ADD COLUMN userid INT,
	ADD CONSTRAINT fk_users 
	FOREIGN KEY (userId)
	REFERENCES users(id)
	ON DELETE CASCADE;
	
# --- !Downs
ALTER TABLES geologs DROP COLUMN userid;