create table s3_video_credential (
  id integer auto_increment primary key,
  access_key_id varchar(20) not null,
  secret_access_key varchar(40) not null,
  bucket varchar(64) not null,
  created_at timestamp not null default sysdate,
  updated_at timestamp not null default sysdate
)