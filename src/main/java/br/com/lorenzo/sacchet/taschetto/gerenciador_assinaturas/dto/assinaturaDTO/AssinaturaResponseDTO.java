package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.assinaturaDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaResponseDTO {
    private Long idAssinatura;
    private String nome;
    private BigDecimal valor;
    private String cicloPagamento;
    private LocalDate proximaCobranca;

    private Long idUsuario;
    private String nomeUsuario;

    private Long idCategoria;
    private String nomeCategoria;

    private Long idMetodoPago;
    private String nomeMetodoPagamento;

    private List<Long> idsTag;
    private List<String> nomesTags;
}
