package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model;

import jakarta.persistence.*;
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

@Entity
@Table(name = "assinatura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_assinatura")
    private Long idAssinatura;

    @NotBlank(message = "Nome da assinatura é obrigatório")
    @Size(max = 100, message = "Nome da assinatura deve ter no máximo 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotBlank(message = "Ciclo de pagamento é obrigatório")
    @Size(max = 30, message = "Ciclo de pagamento deve ter no máximo 30 caracteres")
    @Column(name = "ciclo_pagamento", nullable = false, length = 30)
    private String cicloPagamento;

    @NotNull(message = "Próxima cobrança é obrigatória")
    @Column(name = "proxima_cobranca", nullable = false)
    private LocalDate proximaCobranca;

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull(message = "Categoria é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPagamento metodoPagamento;

    @OneToMany(mappedBy = "assinatura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pagamento> pagamentos;

    // Relacionamento N:N com Tags
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ASSINATURA_TAG",
        joinColumns = @JoinColumn(name = "id_assinatura"),
        inverseJoinColumns = @JoinColumn(name = "id_tag")
    )
    private List<Tag> tags;
}
