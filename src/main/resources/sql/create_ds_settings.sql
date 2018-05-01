create table ds_settings (
  id integer default ds_settings_id_seq.nextval primary key,
  name varchar(128) not null,
  executionpath varchar(256) not null,
  executiontype char(1) not null,
  studiomode char(1) not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)