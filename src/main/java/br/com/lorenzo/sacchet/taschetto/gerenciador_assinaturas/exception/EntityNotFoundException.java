package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, Long id) {
        super(String.format("%s não encontrado com id: %d", entity, id));
    }

    public EntityNotFoundException(String entity, String field, String value) {
        super(String.format("%s não encontrado com %s: %s", entity, field, value));
    }
}
