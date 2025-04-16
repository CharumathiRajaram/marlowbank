CREATE TABLE marlow.accounts (
	id bigserial NOT NULL,
	"name" varchar(100) NOT NULL,
	phone_number varchar(15) NOT NULL,
	balance numeric(12, 2) NOT NULL DEFAULT 0.00,
	"password" text NOT NULL,
	"version" int NOT NULL DEFAULT 0,
	created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT accounts_pk PRIMARY KEY (id)
);
CREATE TYPE operation_type AS ENUM ('deposit', 'withdraw');
CREATE TABLE marlow."transaction" (
	id SERIAL PRIMARY KEY,
    transaction_id UUID DEFAULT gen_random_uuid(),
    account_id BIGINT NOT NULL,
    operation operation_type NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance_after NUMERIC(12, 2) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES marlow.accounts(id) ON DELETE CASCADE
);