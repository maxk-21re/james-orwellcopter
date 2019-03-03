# --- !Ups
ALTER TABLE geologs
	ADD COLUMN clusterid INT,
	ADD CONSTRAINT fk_clusters
	FOREIGN KEY (clusterid)
	REFERENCES clusters(id);

# --- !Downs
ALTER TABLE geologs DROP COLUMN clusterid;
