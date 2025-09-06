package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "TAG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag")
    private Long idTag;

    @NotBlank(message = "Nome da tag é obrigatório")
    @Size(max = 50, message = "Nome da tag deve ter no máximo 50 caracteres")
    @Column(name = "nome", nullable = false, unique = true, length = 50)
    private String nome;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Assinatura> assinaturas;
}
