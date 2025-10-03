package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Pagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByAssinatura(Assinatura assinatura);
    List<Pagamento> findByDataPagamentoBetween(ZonedDateTime dataInicio, ZonedDateTime dataFim);
    List<Pagamento> findByAssinaturaAndDataPagamentoBetween(Assinatura assinatura, ZonedDateTime dataInicio, ZonedDateTime dataFim);
}
