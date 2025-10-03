package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Categoria;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    List<Assinatura> findByUsuario(Usuario usuario);
    List<Assinatura> findByCategoria(Categoria categoria);
    List<Assinatura> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria);
    List<Assinatura> findByProximaCobrancaLessThanEqual(LocalDate data);
    List<Assinatura> findByTagsContaining(Tag tag);
}
