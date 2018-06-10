create table scrum_toolbuttons (
  id integer auto_increment primary key,
  label varchar(128) not null,
  color char(7) not null,
  url varchar(256) not null,
  sort_order integer not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)