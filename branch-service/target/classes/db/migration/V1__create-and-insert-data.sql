CREATE TABLE IF NOT EXISTS branch (
    id SERIAL PRIMARY KEY,
    data JSONB
);

COPY branch(data)
FROM 'C:/Users/Public/branch-data.json'
WITH (FORMAT csv, DELIMITER E'\x02', QUOTE E'\x01');
