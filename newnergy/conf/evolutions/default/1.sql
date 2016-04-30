# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table data (
  id                        bigint not null,
  k_wh                      float,
  cost                      float,
  k_w                       float,
  genk_w                    float,
  genk_wh                   float,
  k_varh                    float,
  k_var                     float,
  julian_day                smallint,
  is_start_day              boolean,
  is_end_day                boolean,
  date                      timestamp,
  date_value                bigint,
  daytype                   varchar(255),
  meter_id                  bigint,
  constraint pk_data primary key (id))
;

create table linked_account (
  id                        bigint not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table meter (
  id                        bigint not null,
  project_id                bigint,
  meter_name                varchar(255),
  description               varchar(255),
  start_date                timestamp,
  end_date                  timestamp,
  constraint pk_meter primary key (id))
;

create table project (
  id                        bigint not null,
  user_id                   bigint not null,
  title                     varchar(255),
  description               varchar(255),
  constraint pk_project primary key (id))
;

create table security_role (
  id                        bigint not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('PR','EV')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  last_login                timestamp,
  active                    boolean,
  email_validated           boolean,
  constraint pk_users primary key (id))
;

create table user_permission (
  id                        bigint not null,
  value                     varchar(255),
  constraint pk_user_permission primary key (id))
;


create table project_users (
  project_id                     bigint not null,
  users_id                       bigint not null,
  constraint pk_project_users primary key (project_id, users_id))
;

create table users_security_role (
  users_id                       bigint not null,
  security_role_id               bigint not null,
  constraint pk_users_security_role primary key (users_id, security_role_id))
;

create table users_user_permission (
  users_id                       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_users_user_permission primary key (users_id, user_permission_id))
;
create sequence data_seq;

create sequence linked_account_seq;

create sequence meter_seq;

create sequence project_seq;

create sequence security_role_seq;

create sequence token_action_seq;

create sequence users_seq;

create sequence user_permission_seq;

alter table data add constraint fk_data_meter_1 foreign key (meter_id) references meter (id) on delete restrict on update restrict;
create index ix_data_meter_1 on data (meter_id);
alter table linked_account add constraint fk_linked_account_user_2 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_linked_account_user_2 on linked_account (user_id);
alter table meter add constraint fk_meter_project_3 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_meter_project_3 on meter (project_id);
alter table project add constraint fk_project_users_4 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_project_users_4 on project (user_id);
alter table token_action add constraint fk_token_action_targetUser_5 foreign key (target_user_id) references users (id) on delete restrict on update restrict;
create index ix_token_action_targetUser_5 on token_action (target_user_id);



alter table project_users add constraint fk_project_users_project_01 foreign key (project_id) references project (id) on delete restrict on update restrict;

alter table project_users add constraint fk_project_users_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists data;

drop table if exists linked_account;

drop table if exists meter;

drop table if exists project;

drop table if exists project_users;

drop table if exists security_role;

drop table if exists token_action;

drop table if exists users;

drop table if exists users_security_role;

drop table if exists users_user_permission;

drop table if exists user_permission;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists data_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists meter_seq;

drop sequence if exists project_seq;

drop sequence if exists security_role_seq;

drop sequence if exists token_action_seq;

drop sequence if exists users_seq;

drop sequence if exists user_permission_seq;

