package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "CATEGORIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(max = 50, message = "Nome da categoria deve ter no máximo 50 caracteres")
    @Column(name = "nome", nullable = false, unique = true, length = 50)
    private String nome;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assinatura> assinaturas;
}
