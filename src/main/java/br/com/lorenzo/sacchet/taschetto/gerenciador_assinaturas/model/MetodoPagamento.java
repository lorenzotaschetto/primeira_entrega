package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "METODO_PAGAMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Long idMetodoPago;

    @NotBlank(message = "Nome personalizado é obrigatório")
    @Size(max = 50, message = "Nome personalizado deve ter no máximo 50 caracteres")
    @Column(name = "nome_personalizado", nullable = false, length = 50)
    private String nomePersonalizado;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 30, message = "Tipo deve ter no máximo 30 caracteres")
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Size(max = 50, message = "Informação adicional deve ter no máximo 50 caracteres")
    @Column(name = "info_adicional", length = 50)
    private String infoAdicional;

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "metodoPagamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assinatura> assinaturas;
}
