package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.metodoPagamentoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagamentoResponseDTO {

    private Long idMetodoPago; // ID gerado pelo banco

    private String nomePersonalizado;

    private String tipo;

    private String infoAdicional;


}
