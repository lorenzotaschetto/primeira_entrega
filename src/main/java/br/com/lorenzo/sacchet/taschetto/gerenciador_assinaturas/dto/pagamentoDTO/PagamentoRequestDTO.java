package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.pagamentoDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {

    @NotNull(message = "ID da assinatura é obrigatório")
    private Long idAssinatura;

    @NotNull(message = "Valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
    private BigDecimal valorPago;
}