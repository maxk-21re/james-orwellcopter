# --- !Ups
ALTER TABLE geologs 
	ADD COLUMN clusterid INT,
	ADD CONSTRAINT fk_clusters
	FOREIGN KEY (clusterid)
	REFERENCES clusters(id);
	
# --- !Downs
ALTER TABLES geologs DROP COLUMN clusterid;
