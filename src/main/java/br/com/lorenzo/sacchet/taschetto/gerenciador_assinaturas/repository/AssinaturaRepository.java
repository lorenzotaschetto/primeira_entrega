package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Categoria;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Tag;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    List<Assinatura> findByUsuario(Usuario usuario);
    List<Assinatura> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria);
    List<Assinatura> findByCategoriaAndUsuario(Categoria categoria, Usuario usuarioLogado);
    List<Assinatura> findByUsuarioAndProximaCobrancaLessThanEqual(Usuario usuarioLogado, LocalDate data);
    List<Assinatura> findByUsuarioAndTagsContaining(Usuario usuarioLogado, Tag tag);
}
