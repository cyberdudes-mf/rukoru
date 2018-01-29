create table repositorydb_settings (
  id integer auto_increment primary key,
  instance_name varchar(64) not null,
  endpoint varchar(128) not null,
  port integer not null,
  username varchar(64) not null,
  password varchar(64) not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)