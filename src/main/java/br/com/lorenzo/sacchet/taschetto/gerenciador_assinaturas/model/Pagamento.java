package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "PAGAMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long idPagamento;

    @NotNull(message = "Data de pagamento é obrigatória")
    @Column(name = "data_pagamento", nullable = false)
    private ZonedDateTime dataPagamento;

    @NotNull(message = "Valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @NotNull(message = "Assinatura é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assinatura", nullable = false)
    private Assinatura assinatura;
}
