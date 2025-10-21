package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioChangePasswordDTO(

        @NotBlank String senhaAtual,

        @NotBlank @Size(min = 8) String novaSenha

) {}
