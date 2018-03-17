select
  ds_settings.id,
  ds_settings.name,
  ds_settings.executionpath,
  ds_settings.executiontype,
  ds_settings.created_at,
  ds_settings.updated_at
from
  preferences
inner join ds_settings
  on  preferences.category = 'DSSetting'
  and ds_settings.id = preferences.value
order by
  preferences.key