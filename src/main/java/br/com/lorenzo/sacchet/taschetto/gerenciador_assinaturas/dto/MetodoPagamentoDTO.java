package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagamentoDTO {
    private Long idMetodoPago;

    @NotBlank(message = "Nome personalizado é obrigatório")
    @Size(max = 50, message = "Nome personalizado deve ter no máximo 50 caracteres")
    private String nomePersonalizado;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 30, message = "Tipo deve ter no máximo 30 caracteres")
    private String tipo;

    @Size(max = 50, message = "Informação adicional deve ter no máximo 50 caracteres")
    private String infoAdicional;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long idUsuario;
}
