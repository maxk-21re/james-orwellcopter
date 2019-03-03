# --- !Ups
ALTER TABLE clusters
	ADD COLUMN shell GEOMETRY;

# --- !Downs
ALTER TABLE clusters DROP COLUMN shell;
