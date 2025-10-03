package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {
    private Long idPagamento;

    @NotNull(message = "Data de pagamento é obrigatória")
    private ZonedDateTime dataPagamento;

    @NotNull(message = "Valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
    private BigDecimal valorPago;

    @NotNull(message = "ID da assinatura é obrigatório")
    private Long idAssinatura;

    // Para resposta com dados relacionados
    private String nomeAssinatura;
    private String nomeUsuario;
}
