create table ds_settings (
  id integer auto_increment primary key,
  name varchar(128) not null,
  dshome varchar(256) not null,
  exection char(1) not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)