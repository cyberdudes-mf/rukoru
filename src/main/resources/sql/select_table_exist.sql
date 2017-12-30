select
  count(id) > 0 as result
from
  information_schema.tables
where
      table_schema = 'PUBLIC'
  and table_name = ?
