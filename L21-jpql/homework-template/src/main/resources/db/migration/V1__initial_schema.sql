create table address
(
    id     bigserial not null primary key,
    street varchar(255)
);

create table client
(
    id         bigserial not null primary key,
    name       varchar(255),
    address_id bigint references address (id)
);

create table phone
(
    id        bigserial not null primary key,
    number    varchar(255),
    client_id bigint references client (id)
);