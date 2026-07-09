ALTER TABLE doctors
    ADD COLUMN activation_token VARCHAR(255) NULL,
    ADD COLUMN activation_token_expires_at DATETIME NULL;

ALTER TABLE doctors
    ADD CONSTRAINT uk_doctors_activation_token UNIQUE (activation_token);