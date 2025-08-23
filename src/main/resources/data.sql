insert into GENRES (name)
select 'Комедия'
where not exists (
    select 1 from GENRES where NAME = 'Комедия'
);
insert into GENRES (name)
select 'Драма'
where not exists (
    select 1 from GENRES where NAME = 'Драма'
);
insert into GENRES (name)
select 'Мультфильм'
where not exists (
    select 1 from GENRES where NAME = 'Мультфильм'
);
insert into GENRES (name)
select 'Триллер'
where not exists (
    select 1 from GENRES where NAME = 'Триллер'
);
insert into GENRES (name)
select 'Документальный'
where not exists (
    select 1 from GENRES where NAME = 'Документальный'
);
insert into GENRES (name)
select 'Боевик'
where not exists (
    select 1 from GENRES where NAME = 'Боевик'
);

INSERT INTO rating_mpa (mpa_name)
SELECT 'G' WHERE NOT EXISTS (
    SELECT 1 FROM rating_mpa WHERE mpa_name = 'G'
);

INSERT INTO rating_mpa (mpa_name)
SELECT 'PG' WHERE NOT EXISTS (
    SELECT 1 FROM rating_mpa WHERE mpa_name = 'PG'
);

INSERT INTO rating_mpa (mpa_name)
SELECT 'PG-13' WHERE NOT EXISTS (
    SELECT 1 FROM rating_mpa WHERE mpa_name = 'PG-13'
);

INSERT INTO rating_mpa (mpa_name)
SELECT 'R' WHERE NOT EXISTS (
    SELECT 1 FROM rating_mpa WHERE mpa_name = 'R'
);

INSERT INTO rating_mpa (mpa_name)
SELECT 'NC-17' WHERE NOT EXISTS (
    SELECT 1 FROM rating_mpa WHERE mpa_name = 'NC-17'
);

