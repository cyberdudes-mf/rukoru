update
  auth_settings
set
  account = ?,
  access_key_id = ?,
  secret_access_key = ?
where
      id = ?
  and updated_at = ?