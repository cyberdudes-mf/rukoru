create table preferences (
  id integer auto_increment primary key,
  category varchar(64) not null,
  key varchar(64) not null,
  value varchar(512) not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)