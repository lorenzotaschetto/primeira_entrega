package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosAutenticacaoDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String senha;
}
