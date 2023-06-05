create table person
(
    person_id   bigserial
        primary key,
    age         integer,
    avatar_id   bigint,
    city        varchar(255) not null,
    gender      varchar(255) not null,
    name        varchar(255) not null,
    nickname    varchar(255) not null,
    password    varchar(255) not null,
    secret_word varchar(255) not null
);

alter table person
    owner to root;

create table event
(
    event_id         bigserial
        primary key,
    address          varchar(255),
    city             varchar(255),
    date             timestamp(6),
    description      varchar(255),
    game             varchar(255),
    max_age          integer,
    max_person_count integer,
    min_age          integer,
    name             varchar(255),
    host             bigint
        constraint fkm0aw83wv9g1dppyied2ojaw6u
            references person
);

alter table event
    owner to root;

create table person_banned_in_events
(
    person_id bigint not null
        constraint fkm23b567ceeow0stkn1hhi0p4d
            references person,
    event_id  bigint not null
        constraint fkcvp85ev3mncxxde9i03mlgl8j
            references event
);

alter table person_banned_in_events
    owner to root;

create table role
(
    role_id bigserial
        primary key,
    name    varchar(255)
);

alter table role
    owner to root;

create table message
(
    message_id bigserial
        primary key,
    date_time  timestamp(6),
    text       varchar(255),
    event      bigint not null
        constraint fkp1gut6tyhyfhsoy9wfw0fww5y
            references event,
    person_id  bigint not null
        constraint fkrjg2ug55rdo338ks6514fw9qy
            references person
);

alter table message
    owner to root;

create table members_events
(
    person_id bigint not null
        constraint fkre7shn8g2xxg0py13r0gkmokr
            references person,
    event_id  bigint not null
        constraint fk1dki5927yi5ojy467ddljcmju
            references event
);

alter table members_events
    owner to root;

create table person_roles
(
    role_id   bigint not null
        constraint fkeylgk8k8teqaxmadsiaixinfe
            references role,
    person_id bigint not null
        constraint fks955luj19xyjwi3s1omo1pgh4
            references person
);

alter table person_roles
    owner to root;

create table token
(
    token_id  bigserial
        primary key,
    value     varchar(255),
    person_id bigint
        constraint fk9qkicjk44c4wheb7nhxbn0aha
            references person
);

alter table token
    owner to root;

create table items
(
    item_id  bigserial
        primary key,
    marked   boolean,
    name     varchar(255),
    event_id bigint
        constraint fk3yjvw7toyhnlyb1yry9u7fcj9
            references event
);

alter table items
    owner to root;


