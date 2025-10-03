package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long idCategoria;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(max = 50, message = "Nome da categoria deve ter no máximo 50 caracteres")
    private String nome;
}
