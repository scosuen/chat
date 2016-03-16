-- Create table
create table cc_session
(
  session_id  		number not null,
  create_time 		date,
  create_user_id 	number,
  create_user_name 	VARCHAR2(30),
  update_time      	DATE
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table cc_session
  add constraint PK_cc_session_id primary key (SESSION_ID);
  
-- Create sequence 
create sequence cc_session_seq
minvalue 1
start with 1
increment by 1;

-- Create table
create table cc_session_users
(
  session_users_id number not null,
  session_id       number,
  user_id          number,
  join_time        date,
  user_name        VARCHAR2(30)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table cc_session_users
  add constraint pk_cc_session_users_id primary key (SESSION_USERS_ID);
  
-- Create sequence 
create sequence cc_session_users_seq
minvalue 1
start with 1
increment by 1;

-- Create sequence 
create sequence cc_session_messages_seq
minvalue 1
start with 1
increment by 1;
  
-- Create table
create table cc_session_messages
(
  session_messages_id number not null,
  session_id          number,
  send_user_id        number,
  message             varchar2(500),
  send_time           date,
  send_user_name      VARCHAR2(30)
)
;
-- Create/Recreate primary, unique and foreign key constraints 
alter table cc_session_messages
  add constraint pk_cc_session_messages_id primary key (SESSION_MESSAGES_ID);