CREATE TABLE IF NOT EXISTS tarefas (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    responsavel_id BIGINT,
    prioridade VARCHAR(10) NOT NULL,
    deadline DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ANDAMENTO',
    data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    data_update TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_responsavel FOREIGN KEY (responsavel_id)
    REFERENCES usuarios(id) ON DELETE SET NULL,

    CONSTRAINT ck_prioridade CHECK (prioridade IN ('ALTA', 'MEDIA', 'BAIXA')),
    CONSTRAINT ck_status CHECK (status IN ('ANDAMENTO', 'CONCLUIDA'))
    );

COMMENT ON TABLE tarefas IS 'Tabela de tarefas vinculadas aos usuários';
COMMENT ON COLUMN tarefas.prioridade IS 'Prioridade das tarefas: ALTA, MEDIA, BAIXA';
COMMENT ON COLUMN tarefas.status IS 'Situação da tarefa: ANDAMENTO ou CONCLUIDA';