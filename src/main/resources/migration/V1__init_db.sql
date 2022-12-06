create table if not exists analytic
(
    id       bigserial not null,
    date     timestamp,
    group_id int8,
    primary key (id)
);
create table if not exists analytic_exit_users
(
    analytic_id   int8 not null,
    exit_users_id int8 not null,
    primary key (analytic_id, exit_users_id)
);
create table if not exists analytic_joined_users
(
    analytic_id     int8 not null,
    joined_users_id int8 not null,
    primary key (analytic_id, joined_users_id)
);
create table if not exists groups
(
    id                     bigserial    not null,
    additional_information varchar(255),
    group_name             varchar(255),
    vk_link                varchar(255) not null,
    primary key (id)
);
create table if not exists subscription
(
    id   bigserial not null,
    type varchar(255),
    primary key (id)
);
create table if not exists vk_users
(
    id                     bigserial not null,
    additional_information varchar(255),
    average_of_visits      int8,
    been_here_one_time     boolean,
    chat_name              varchar(255),
    first_name             varchar(255),
    game_account           int8,
    games_count            int8,
    has_not_visit_yet      boolean,
    interest_games         varchar(255),
    is_in_the_chat         boolean,
    last_contact           timestamp,
    last_name              varchar(255),
    tg_link                varchar(255),
    vk_link                varchar(255),
    was_contacted          boolean,
    who_invite_him         varchar(255),
    primary key (id)
);
create table if not exists vk_users_groups
(
    vk_users_id int8 not null,
    groups_id   int8 not null,
    primary key (vk_users_id, groups_id)
);
create table if not exists vk_users_subscriptions
(
    user_id          int8 not null,
    subscriptions_id int8 not null,
    primary key (user_id, subscriptions_id)
);
alter table if exists groups
    add constraint UK_b2b33nb26pi1if7i4ylaj4re8 unique (vk_link);
alter table if exists subscription
    add constraint UK_n2nisf1h9d07x24w5b7shu53d unique (type);
alter table if exists vk_users
    add constraint UK_74lssptm4x5sdj45umq9a1wyi unique (vk_link);
alter table if exists vk_users_subscriptions
    add constraint UK_sobtwvx49o01l972w6tki25mt unique (subscriptions_id);
alter table if exists analytic
    add constraint FKkvvg7omd68doov46ljcjjuovt foreign key (group_id) references groups;
alter table if exists analytic_exit_users
    add constraint FKsmv3in1g9k1wp7ereclud15u7 foreign key (exit_users_id) references vk_users;
alter table if exists analytic_exit_users
    add constraint FKgcpue834jxjjr6fnxmg561g2o foreign key (analytic_id) references analytic;
alter table if exists analytic_joined_users
    add constraint FKmwbae74ycu110gsmiet9ok9e foreign key (joined_users_id) references vk_users;
alter table if exists analytic_joined_users
    add constraint FKcbmbux1t3kpr8cdmvhpue8b7n foreign key (analytic_id) references analytic;
alter table if exists vk_users_groups
    add constraint FK4j3fqcnpcg4swbvjwcjs6ulnd foreign key (groups_id) references groups;
alter table if exists vk_users_groups
    add constraint FKnh19msg7x87kwg6lug29axp5b foreign key (vk_users_id) references vk_users;
alter table if exists vk_users_subscriptions
    add constraint FKcak7hjwy28qgsbyavbbnxrv2q foreign key (subscriptions_id) references subscription;
alter table if exists vk_users_subscriptions
    add constraint FK7c9kdsd0hv1aionl7yd0kl9tn foreign key (user_id) references vk_users;

create table if not exists users
(
    username varchar(255) not null primary key,
    password varchar(255) not null,
    enabled  boolean      not null
);
create table if not exists authorities
(
    username  varchar(255) not null,
    authority varchar(255) not null,
    foreign key (username) references users (username);