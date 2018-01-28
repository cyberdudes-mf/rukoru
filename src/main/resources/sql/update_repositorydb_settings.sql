update
  repositorydb_settings
set
  instance_name = ?,
  endpoint = ?,
  port = ?,
  username = ?,
  password = ?
where
      id = ?
  and updated_at = ?