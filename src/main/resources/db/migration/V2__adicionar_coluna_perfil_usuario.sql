-- V2__adicionar_coluna_perfil_usuario.sql

-- Adiciona a nova coluna 'perfil' à tabela 'usuario'
-- o valor padrão 'ROLE_USUARIO' garante que novos registros terão um perfil válido.
ALTER TABLE usuario ADD COLUMN perfil VARCHAR(255) NOT NULL DEFAULT 'ROLE_USUARIO';