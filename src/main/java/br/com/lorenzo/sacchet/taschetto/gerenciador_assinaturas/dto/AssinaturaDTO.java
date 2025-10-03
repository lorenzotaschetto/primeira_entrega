package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaDTO {
    private Long idAssinatura;

    @NotBlank(message = "Nome da assinatura é obrigatório")
    @Size(max = 100, message = "Nome da assinatura deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotBlank(message = "Ciclo de pagamento é obrigatório")
    @Size(max = 30, message = "Ciclo de pagamento deve ter no máximo 30 caracteres")
    private String cicloPagamento;

    @NotNull(message = "Próxima cobrança é obrigatória")
    private LocalDate proximaCobranca;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long idUsuario;

    @NotNull(message = "ID da categoria é obrigatório")
    private Long idCategoria;

    private Long idMetodoPago;

    private List<Long> idsTag;

    // Para resposta com dados relacionados
    private String nomeUsuario;
    private String nomeCategoria;
    private String nomeMetodoPagamento;
    private List<String> nomesTags;
}
