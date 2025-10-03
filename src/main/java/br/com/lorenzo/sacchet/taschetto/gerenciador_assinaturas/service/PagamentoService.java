package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Pagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Assinatura;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.PagamentoDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.PagamentoRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AssinaturaService assinaturaService;

    public List<PagamentoDTO> listarTodos() {
        return pagamentoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PagamentoDTO> buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public PagamentoDTO salvar(PagamentoDTO pagamentoDTO) {
        Pagamento pagamento = convertToEntity(pagamentoDTO);
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        return convertToDTO(pagamentoSalvo);
    }

    public PagamentoDTO atualizar(Long id, PagamentoDTO pagamentoDTO) {
        return pagamentoRepository.findById(id)
                .map(pagamento -> {
                    pagamento.setDataPagamento(pagamentoDTO.getDataPagamento());
                    pagamento.setValorPago(pagamentoDTO.getValorPago());
                    pagamento.setAssinatura(assinaturaService.buscarEntidadePorId(pagamentoDTO.getIdAssinatura()));
                    Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamento);
                    return convertToDTO(pagamentoAtualizado);
                })
                .orElseThrow(() -> new EntityNotFoundException("Pagamento", id));
    }

    public void deletar(Long id) {
        if (!pagamentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagamento", id);
        }
        pagamentoRepository.deleteById(id);
    }

    public List<PagamentoDTO> buscarPorAssinatura(Long idAssinatura) {
        Assinatura assinatura = assinaturaService.buscarEntidadePorId(idAssinatura);
        return pagamentoRepository.findByAssinatura(assinatura).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PagamentoDTO> buscarPorPeriodo(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        return pagamentoRepository.findByDataPagamentoBetween(dataInicio, dataFim).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PagamentoDTO> buscarPorAssinaturaEPeriodo(Long idAssinatura, ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        Assinatura assinatura = assinaturaService.buscarEntidadePorId(idAssinatura);
        return pagamentoRepository.findByAssinaturaAndDataPagamentoBetween(assinatura, dataInicio, dataFim).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal calcularTotalPagoPorAssinatura(Long idAssinatura) {
        List<Pagamento> pagamentos = pagamentoRepository.findByAssinatura(assinaturaService.buscarEntidadePorId(idAssinatura));
        return pagamentos.stream()
                .map(Pagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPagoPorUsuario(Long idUsuario) {
        List<Assinatura> assinaturas = assinaturaService.buscarPorUsuario(idUsuario).stream()
                .map(dto -> assinaturaService.buscarEntidadePorId(dto.getIdAssinatura()))
                .collect(Collectors.toList());
        return assinaturas.stream()
                .flatMap(assinatura -> pagamentoRepository.findByAssinatura(assinatura).stream())
                .map(Pagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPagoPorPeriodo(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        List<Pagamento> pagamentos = pagamentoRepository.findByDataPagamentoBetween(dataInicio, dataFim);
        return pagamentos.stream()
                .map(Pagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPagoPorUsuarioEPeriodo(Long idUsuario, ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        List<Assinatura> assinaturas = assinaturaService.buscarPorUsuario(idUsuario).stream()
                .map(dto -> assinaturaService.buscarEntidadePorId(dto.getIdAssinatura()))
                .collect(Collectors.toList());
        return assinaturas.stream()
                .flatMap(assinatura -> pagamentoRepository.findByAssinaturaAndDataPagamentoBetween(assinatura, dataInicio, dataFim).stream())
                .map(Pagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public PagamentoDTO registrarPagamento(Long idAssinatura, BigDecimal valorPago) {
        Assinatura assinatura = assinaturaService.buscarEntidadePorId(idAssinatura);

        Pagamento pagamento = new Pagamento();
        pagamento.setAssinatura(assinatura);
        pagamento.setValorPago(valorPago);
        pagamento.setDataPagamento(ZonedDateTime.now());

        // Atualizar próxima cobrança da assinatura
        LocalDate proximaCobranca = calcularProximaCobranca(assinatura);
        assinatura.setProximaCobranca(proximaCobranca);
        assinaturaService.salvar(assinaturaService.convertEntityToDTO(assinatura)); // Usar método público de conversão

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        return convertToDTO(pagamentoSalvo);
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
            default -> dataAtual.plusMonths(1); // Default para mensal
        };
    }

    // Métodos de conversão
    private PagamentoDTO convertToDTO(Pagamento pagamento) {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setIdPagamento(pagamento.getIdPagamento());
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setValorPago(pagamento.getValorPago());
        dto.setIdAssinatura(pagamento.getAssinatura().getIdAssinatura());

        // Dados para exibição
        dto.setNomeAssinatura(pagamento.getAssinatura().getNome());
        dto.setNomeUsuario(pagamento.getAssinatura().getUsuario().getNome());

        return dto;
    }

    private Pagamento convertToEntity(PagamentoDTO dto) {
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPagamento(dto.getIdPagamento());
        pagamento.setDataPagamento(dto.getDataPagamento());
        pagamento.setValorPago(dto.getValorPago());

        // Buscar assinatura
        pagamento.setAssinatura(assinaturaService.buscarEntidadePorId(dto.getIdAssinatura()));

        return pagamento;
    }
}
