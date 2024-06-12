CREATE TABLE financial_institution (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    modified_on TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL
);

INSERT INTO financial_institution (created_on, modified_on, name) VALUES
	(current_timestamp, current_timestamp, 'CAIXA ECONOMICA FEDERAL'),
	(current_timestamp, current_timestamp, 'BANCO DO BRASIL S.A.');