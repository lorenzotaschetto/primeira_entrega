package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception;

public class EntityInUseException extends RuntimeException {
    public EntityInUseException(String message) {
        super(message);
    }

    public EntityInUseException(String entity, Long id) {
        super(String.format("Não é possível excluir %s que possui dependências vinculadas", entity));
    }
}
