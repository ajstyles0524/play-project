# --- !Ups
create table "player" (
                          "id" bigint generated by default as identity(start with 1) not null primary key,
                          "name" varchar not null,
                          "country" varchar not null,
                          "role" varchar not null
);

# --- !Downs

drop table "player" if exists;
