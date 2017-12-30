create table auth_settings (
  id integer auto_increment primary key,
  account varchar(12) not null,
  access_key_id varchar(20) not null,
  secret_access_key varchar(40) not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)