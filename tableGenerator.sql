create table products
(
    ean   varchar(15) not null
        primary key,
    name  varchar(255),
    brand varchar(255)
);

alter table products
    owner to postgres;

create table profiles
(
    email   varchar(255) not null
        primary key,
    name    varchar(255),
    surname varchar(255)
);

alter table profiles
    owner to postgres;

create table experts
(
    expert_id integer not null
        primary key,
    name       varchar(255),
    surname    varchar(255),
    email      varchar(255)
);

alter table experts
    owner to postgres;

create table tickets
(
    ticket_id      integer not null
        primary key,
    profile_id     varchar(255)
        constraint tickets_profiles_email_fk
            references profiles,
    ean             varchar(15)
        references products,
    priority_level integer
        constraint tickets_priority_level_check
            check ((priority_level >= 0) AND (priority_level <= 4)),
    expert_id      integer
        constraint tickets_expert_id_fkey
            references experts,
    status          varchar(15)
        constraint tickets_status_check
            check ((status)::text = ANY
        ((ARRAY ['open'::character varying, 'closed'::character varying, 'in_progress'::character varying, 'reopened'::character varying, 'resolved'::character varying])::text[])),
    "creation_date"  timestamp
);

alter table tickets
    owner to postgres;

create table tickets_history
(
    history_id integer not null
        constraint tickets_history_pkey
            primary key,
    ticket_id  integer
        constraint tickets_history_ticket_id_fkey
            references tickets,
    old_status varchar(15)
        constraint tickets_history_old_status_check
            check ((old_status)::text = ANY
        (ARRAY [('open'::character varying)::text, ('closed'::character varying)::text, ('in_progress'::character varying)::text, ('reopened'::character varying)::text, ('resolved'::character varying)::text])),
    new_status varchar(15)
        constraint tickets_history_new_status_check
            check ((new_status)::text = ANY
                   (ARRAY [('open'::character varying)::text, ('closed'::character varying)::text, ('in_progress'::character varying)::text, ('reopened'::character varying)::text, ('resolved'::character varying)::text])),
    date_time    timestamp
);

alter table tickets_history
    owner to postgres;

create table messages
(
    message_id integer not null
        primary key,
    ticket_id  integer
        constraint messages_ticket_id_fkey
            references tickets,
    from_user  boolean,
    text        varchar(1023),
    datetime    timestamp
);

alter table messages
    owner to postgres;

create table attachments
(
    attachment_id integer not null
        primary key,
    message_id    integer
        constraint attachments_message_id_fkey
            references messages,
    type           varchar(15),
    size           integer,
    data_bin      bytea,
    datetime       timestamp
);

alter table attachments
    owner to postgres;

create table sectors
(
    sector_id integer not null
        primary key,
    name varchar(31)
);

create table expert_sector
(
    expert_id integer not null
        constraint experts_sectors_expert_id_fkey
        references experts,
    sector_id integer not null
        constraint experts_sectors_sector_id_fkey
        references sectors,
    primary key (expert_id, sector_id)
)