package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.pagamentoDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoUpdateDTO {

    @NotNull(message = "Valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
    private BigDecimal valorPago;

    @NotNull(message = "Data do pagamento é obrigatória")
    private LocalDate dataPagamento;
}