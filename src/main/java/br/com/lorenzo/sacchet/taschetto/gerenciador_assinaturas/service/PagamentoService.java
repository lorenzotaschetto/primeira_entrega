package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.pagamentoDTO.PagamentoRequestDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.pagamentoDTO.PagamentoResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.pagamentoDTO.PagamentoUpdateDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.infra.security.SecurityHelper;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Pagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.AssinaturaRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private SecurityHelper securityHelper;
    @Autowired
    private Clock clock;


    public List<PagamentoResponseDTO> listarTodos() {
        return pagamentoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("@securityService.checarPossePagamento(#id)")
    public PagamentoResponseDTO buscarPorId(@Param("id") Long id) {
        Pagamento pagamento = buscarEntidadePorId(id);
        return convertToResponseDTO(pagamento);
    }

    @Transactional
    @PreAuthorize("@securityService.checarPosseAssinatura(#dto.idAssinatura)")
    public PagamentoResponseDTO registrarPagamento(@Param("dto") PagamentoRequestDTO dto) {
        Assinatura assinatura = assinaturaRepository.findById(dto.getIdAssinatura())
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", dto.getIdAssinatura()));
        Pagamento pagamento = new Pagamento();
        pagamento.setAssinatura(assinatura);
        pagamento.setValorPago(dto.getValorPago());
        pagamento.setDataPagamento(ZonedDateTime.now(clock));

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        LocalDate proximaCobranca = calcularProximaCobranca(assinatura);
        assinatura.setProximaCobranca(proximaCobranca);

        return convertToResponseDTO(pagamentoSalvo);
    }

    @Transactional
    @PreAuthorize("@securityService.checarPossePagamento(#id)")
    public PagamentoResponseDTO atualizar(@Param("id") Long id, PagamentoUpdateDTO dto) {
        Pagamento pagamento = buscarEntidadePorId(id);

        pagamento.setValorPago(dto.getValorPago());
        pagamento.setDataPagamento(dto.getDataPagamento().atStartOfDay(ZoneId.systemDefault()));

        Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamento);
        return convertToResponseDTO(pagamentoAtualizado);
    }

    @Transactional
    @PreAuthorize("@securityService.checarPossePagamento(#id)")
    public void deletar(@Param("id") Long id) {
        pagamentoRepository.deleteById(id);
    }


    @PreAuthorize("@securityService.checarPosseAssinatura(#idAssinatura)")
    public List<PagamentoResponseDTO> buscarPorAssinatura(@Param("idAssinatura") Long idAssinatura) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));
        return pagamentoRepository.findByAssinatura(assinatura).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PagamentoResponseDTO> buscarPorPeriodoDoUsuarioLogado(LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        ZonedDateTime inicioZoned = dataInicio.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime fimZoned = dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault());

        List<Pagamento> pagamentos = pagamentoRepository.findByAssinaturaUsuarioAndDataPagamentoBetween(usuarioLogado, inicioZoned, fimZoned);

        return pagamentos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    @PreAuthorize("@securityService.checarPosseAssinatura(#idAssinatura)")
    public List<PagamentoResponseDTO> buscarPorAssinaturaEPeriodo(@Param("idAssinatura") Long idAssinatura, LocalDate dataInicio, LocalDate dataFim) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));

        ZonedDateTime inicioZoned = dataInicio.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime fimZoned = dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault());

        return pagamentoRepository.findByAssinaturaAndDataPagamentoBetween(assinatura, inicioZoned, fimZoned).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("@securityService.checarPosseAssinatura(#idAssinatura)")
    public BigDecimal calcularTotalPagoPorAssinatura(@Param("idAssinatura") Long idAssinatura) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));
        List<Pagamento> pagamentos = pagamentoRepository.findByAssinatura(assinatura);
        return pagamentos.stream()
                .map(Pagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPagoPeloUsuarioLogado() {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        return pagamentoRepository.sumValorPagoByUsuario(usuarioLogado);
    }

    public BigDecimal calcularTotalPagoPeloUsuarioLogadoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        ZonedDateTime inicioZoned = dataInicio.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime fimZoned = dataFim.atTime(23, 59, 59).atZone(ZoneId.systemDefault());

        return pagamentoRepository.sumValorPagoByUsuarioAndPeriodo(usuarioLogado, inicioZoned, fimZoned);
    }

    private Pagamento buscarEntidadePorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento", id));
    }

    private LocalDate calcularProximaCobranca(Assinatura assinatura) {
        LocalDate dataAtual = assinatura.getProximaCobranca();
        String ciclo = assinatura.getCicloPagamento().toUpperCase();

        return switch (ciclo) {
            case "MENSAL" -> dataAtual.plusMonths(1);
            case "ANUAL" -> dataAtual.plusYears(1);
            case "SEMANAL" -> dataAtual.plusWeeks(1);
            case "TRIMESTRAL" -> dataAtual.plusMonths(3);
            case "SEMESTRAL" -> dataAtual.plusMonths(6);
            default -> dataAtual.plusMonths(1);
        };
    }

    public List<PagamentoResponseDTO> buscarPorUsuario() {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        List<Pagamento> pagamentos = pagamentoRepository.findByAssinaturaUsuario(usuarioLogado);

        return pagamentos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private PagamentoResponseDTO convertToResponseDTO(Pagamento pagamento) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();
        dto.setIdPagamento(pagamento.getIdPagamento());
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setValorPago(pagamento.getValorPago());
        if (pagamento.getAssinatura() != null) {
            dto.setIdAssinatura(pagamento.getAssinatura().getIdAssinatura());
        }
        dto.setNomeAssinatura(pagamento.getAssinatura().getNome());
        dto.setNomeUsuario(pagamento.getAssinatura().getUsuario().getNome());
        dto.setNomeMetodoPago(pagamento.getAssinatura().getMetodoPagamento().getNomePersonalizado());

        if (pagamento.getValorPago().compareTo(pagamento.getAssinatura().getValor()) < 0) {
            dto.setAlerta("O valor pago é menor que o valor da assinatura: " + pagamento.getAssinatura().getValor());
        } else if(pagamento.getValorPago().compareTo(pagamento.getAssinatura().getValor()) > 0){
            dto.setAlerta("O valor pago é maior que o valor da assinatura: " + pagamento.getAssinatura().getValor());
        }
        return dto;
    }
}