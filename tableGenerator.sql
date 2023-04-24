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
    "expertId" integer not null
        primary key,
    name       varchar(255),
    surname    varchar(255),
    sector     varchar(255),
    email      varchar(255)
);

alter table experts
    owner to postgres;

create table tickets
(
    "ticketId"      integer not null
        primary key,
    "profileId"     varchar(255)
        constraint tickets_profiles_email_fk
            references profiles,
    ean             varchar(15)
        references products,
    "priorityLevel" integer
        constraint tickets_priority_level_check
            check (("priorityLevel" >= 0) AND ("priorityLevel" <= 4)),
    "expertId"      integer
        constraint tickets_expert_id_fkey
            references experts,
    status          varchar(15)
        constraint tickets_status_check
            check ((status)::text = ANY
        ((ARRAY ['open'::character varying, 'closed'::character varying, 'in_progress'::character varying, 'reopened'::character varying, 'resolved'::character varying])::text[])),
    "creationDate"  timestamp
);

alter table tickets
    owner to postgres;

create table "ticketsHistory"
(
    "historyId" integer not null
        constraint tickets_history_pkey
            primary key,
    "ticketId"  integer
        constraint tickets_history_ticket_id_fkey
            references tickets,
    "oldStatus" varchar(15)
        constraint tickets_history_old_status_check
            check (("oldStatus")::text = ANY
        (ARRAY [('open'::character varying)::text, ('closed'::character varying)::text, ('in_progress'::character varying)::text, ('reopened'::character varying)::text, ('resolved'::character varying)::text])),
    "newStatus" varchar(15)
        constraint tickets_history_new_status_check
            check (("newStatus")::text = ANY
                   (ARRAY [('open'::character varying)::text, ('closed'::character varying)::text, ('in_progress'::character varying)::text, ('reopened'::character varying)::text, ('resolved'::character varying)::text])),
    datetime    timestamp
);

alter table "ticketsHistory"
    owner to postgres;

create table messages
(
    "messageId" integer not null
        primary key,
    "ticketId"  integer
        constraint messages_ticket_id_fkey
            references tickets,
    "fromUser"  boolean,
    text        varchar(1023),
    datetime    timestamp
);

alter table messages
    owner to postgres;

create table attachments
(
    "attachmentId" integer not null
        primary key,
    "messageId"    integer
        constraint attachments_message_id_fkey
            references messages,
    type           varchar(15),
    size           integer,
    "dataBin"      bytea,
    datetime       timestamp
);

alter table attachments
    owner to postgres;

