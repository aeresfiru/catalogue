create schema if not exists catalogue;

create table catalogue.product
(
    id      SERIAL PRIMARY KEY,
    title   varchar(50) NOT NULL check (length(trim(title)) >= 3),
    details varchar(1000)
)
