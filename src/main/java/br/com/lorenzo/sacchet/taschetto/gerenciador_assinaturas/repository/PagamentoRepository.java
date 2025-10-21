package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Pagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByAssinatura(Assinatura assinatura);
    List<Pagamento> findByDataPagamentoBetween(ZonedDateTime dataInicio, ZonedDateTime dataFim);
    List<Pagamento> findByAssinaturaAndDataPagamentoBetween(Assinatura assinatura, ZonedDateTime dataInicio, ZonedDateTime dataFim);

    @Query("SELECT COALESCE(SUM(p.valorPago), 0) FROM Pagamento p WHERE p.assinatura.usuario = :usuario")
    BigDecimal sumValorPagoByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT COALESCE(SUM(p.valorPago), 0) FROM Pagamento p WHERE p.assinatura.usuario = :usuario AND p.dataPagamento BETWEEN :inicio AND :fim")
    BigDecimal sumValorPagoByUsuarioAndPeriodo(@Param("usuario") Usuario usuario,
                                               @Param("inicio") ZonedDateTime inicio,
                                               @Param("fim") ZonedDateTime fim);

    List<Pagamento> findByAssinaturaUsuarioAndDataPagamentoBetween(Usuario usuario, ZonedDateTime inicio, ZonedDateTime fim);}
