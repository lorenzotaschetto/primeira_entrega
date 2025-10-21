package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nome;
    private String email;
}
