create schema if not exists catalogue;

create table catalogue.t_product
(
    id        SERIAL PRIMARY KEY,
    c_title   varchar(50) NOT NULL check (length(trim(c_title)) >= 3),
    c_details varchar(1000)
)
