create table profiles
(
    email   varchar(255) not null
        constraint profile_pkey
            primary key,
    name    varchar(255),
    surname varchar(255)
);

alter table profiles
    owner to postgres;

INSERT INTO public.profiles (email, name, surname) VALUES ('pippobaudo@polito.it', 'Pippo', 'Baudo');
INSERT INTO public.profiles (email, name, surname) VALUES ('test@test.it', 'A', 'B');
INSERT INTO public.profiles (email, name, surname) VALUES ('massimiliano.pellegrino@polito.it', 'Massimiliano', 'Pellegrino');
INSERT INTO public.profiles (email, name, surname) VALUES ('luigimolinengo@gmail.com', 'Luigi', 'Molinengo');
INSERT INTO public.profiles (email, name, surname) VALUES ('alice@meraviglie.it', 'Alice', 'Nel paese delle meraviglie');
