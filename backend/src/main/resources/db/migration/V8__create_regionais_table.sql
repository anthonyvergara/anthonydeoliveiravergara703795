CREATE TABLE regionais (
    id BIGSERIAL PRIMARY KEY,
    external_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_regionais_external_id ON regionais(external_id);
CREATE INDEX idx_regionais_ativo ON regionais(ativo);

