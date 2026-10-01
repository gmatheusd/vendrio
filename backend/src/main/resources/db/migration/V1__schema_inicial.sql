-- Esquema inicial do Vendrio
-- Regra de ouro: o estoque é a soma de movimentacoes_estoque.

CREATE TABLE usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(120)    NOT NULL,
    email       VARCHAR(160)    NOT NULL UNIQUE,
    senha_hash  VARCHAR(100)    NOT NULL,
    perfil      VARCHAR(20)     NOT NULL CHECK (perfil IN ('ADMIN', 'OPERADOR')),
    ativo       BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE TABLE produtos (
    id              BIGSERIAL PRIMARY KEY,
    sku             VARCHAR(40)     NOT NULL UNIQUE,
    codigo_barras   VARCHAR(20)     UNIQUE,
    nome            VARCHAR(160)    NOT NULL,
    preco_custo     NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    preco_venda     NUMERIC(12, 2)  NOT NULL,
    estoque_minimo  INTEGER         NOT NULL DEFAULT 0,
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,
    version         BIGINT          NOT NULL DEFAULT 0,
    criado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE TABLE fornecedores (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(160)    NOT NULL,
    cnpj        VARCHAR(18)     UNIQUE,
    telefone    VARCHAR(20)
);

CREATE TABLE entradas (
    id              BIGSERIAL PRIMARY KEY,
    fornecedor_id   BIGINT REFERENCES fornecedores (id),
    usuario_id      BIGINT REFERENCES usuarios (id),
    data            TIMESTAMP NOT NULL DEFAULT NOW(),
    observacao      VARCHAR(255)
);

CREATE TABLE itens_entrada (
    id              BIGSERIAL PRIMARY KEY,
    entrada_id      BIGINT          NOT NULL REFERENCES entradas (id),
    produto_id      BIGINT          NOT NULL REFERENCES produtos (id),
    quantidade      INTEGER         NOT NULL CHECK (quantidade > 0),
    custo_unitario  NUMERIC(12, 2)  NOT NULL
);

CREATE TABLE caixas (
    id                      BIGSERIAL PRIMARY KEY,
    usuario_id              BIGINT          REFERENCES usuarios (id),
    aberto_em               TIMESTAMP       NOT NULL DEFAULT NOW(),
    fechado_em              TIMESTAMP,
    valor_inicial           NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    valor_final_informado   NUMERIC(12, 2),
    status                  VARCHAR(20)     NOT NULL CHECK (status IN ('ABERTO', 'FECHADO'))
);

CREATE TABLE movimentos_caixa (
    id              BIGSERIAL PRIMARY KEY,
    caixa_id        BIGINT          NOT NULL REFERENCES caixas (id),
    tipo            VARCHAR(20)     NOT NULL CHECK (tipo IN ('SANGRIA', 'SUPRIMENTO')),
    valor           NUMERIC(12, 2)  NOT NULL CHECK (valor > 0),
    motivo          VARCHAR(255),
    data            TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE TABLE vendas (
    id          BIGSERIAL PRIMARY KEY,
    caixa_id    BIGINT          NOT NULL REFERENCES caixas (id),
    data        TIMESTAMP       NOT NULL DEFAULT NOW(),
    total       NUMERIC(12, 2)  NOT NULL,
    desconto    NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    status      VARCHAR(20)     NOT NULL CHECK (status IN ('FINALIZADA', 'CANCELADA'))
);

CREATE TABLE itens_venda (
    id              BIGSERIAL PRIMARY KEY,
    venda_id        BIGINT      NOT NULL REFERENCES vendas (id),
    produto_id      BIGINT         NOT NULL REFERENCES produtos (id),
    quantidade      INTEGER        NOT NULL CHECK (quantidade > 0),
    preco_unitario  NUMERIC(12, 2)  NOT NULL,
    custo_unitario  NUMERIC(12, 2)  NOT NULL -- custo no momento da venda, para calcular lucro
);

CREATE TABLE pagamentos (
    id          BIGSERIAL PRIMARY KEY,
    venda_id    BIGINT      NOT NULL REFERENCES vendas (id),
    forma       VARCHAR(20)     NOT NULL CHECK (forma IN ('DINHEIRO', 'PIX', 'DEBITO', 'CREDITO')),
    valor       NUMERIC(12, 2)  NOT NULL CHECK (valor > 0)
);

CREATE TABLE movimentacoes_estoque (
    id              BIGSERIAL PRIMARY KEY,
    produto_id      BIGINT      NOT NULL REFERENCES produtos (id),
    tipo            VARCHAR(20) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
    quantidade      INTEGER     NOT NULL,   -- positiva soma, negativa subtrai
    origem_tipo     VARCHAR(20),            -- ENTRADA, VENDA, CANCELAMENTO, AJUSTE
    origem_id       BIGINT,
    data            TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mov_estoque_produto ON movimentacoes_estoque (produto_id);
CREATE INDEX idx_vendas_data ON vendas (data);
CREATE INDEX idx_entradas_data ON entradas (data);