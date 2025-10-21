package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.MetodoPagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamento, Long> {
    List<MetodoPagamento> findByUsuario(Usuario usuario);
    List<MetodoPagamento> findByUsuarioAndTipo(Usuario usuario, String tipo);
}
