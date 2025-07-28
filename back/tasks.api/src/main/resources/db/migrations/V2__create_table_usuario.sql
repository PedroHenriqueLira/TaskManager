CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    documento VARCHAR(20) NOT NULL,
    endereco VARCHAR(250),
    status VARCHAR(7) NOT NULL DEFAULT 'ATIVO',
    data_cadastro TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_update TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    tipo_user VARCHAR(50) NOT NULL DEFAULT 'USER',
    CONSTRAINT ck_status CHECK (status IN ('ATIVO', 'INATIVO'))
    );

COMMENT ON TABLE usuarios IS 'Tabela de usuários com dados pessoais e status';
COMMENT ON COLUMN usuarios.status IS 'Status do usuário (ATIVO ou INATIVO)';