package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.pagamentoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponseDTO {

    private Long idPagamento;
    private ZonedDateTime dataPagamento;
    private BigDecimal valorPago;
    private Long idAssinatura;
    private String nomeAssinatura;
    private String nomeUsuario;
    private String nomeMetodoPago;
    private String alerta;
}