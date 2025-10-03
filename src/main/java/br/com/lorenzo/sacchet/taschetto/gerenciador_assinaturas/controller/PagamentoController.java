package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.controller;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.PagamentoDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "Gerenciamento de histórico de pagamentos das assinaturas")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    @Operation(summary = "Listar todos os pagamentos", description = "Retorna uma lista com todos os pagamentos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de pagamentos retornada com sucesso")
    public ResponseEntity<List<PagamentoDTO>> listarTodos() {
        List<PagamentoDTO> pagamentos = pagamentoService.listarTodos();
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID", description = "Retorna um pagamento específico pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<PagamentoDTO> buscarPorId(
            @Parameter(description = "ID do pagamento") @PathVariable Long id) {
        Optional<PagamentoDTO> pagamento = pagamentoService.buscarPorId(id);
        return pagamento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo pagamento", description = "Registra um novo pagamento no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagamento registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<PagamentoDTO> criar(@Valid @RequestBody PagamentoDTO pagamento, UriComponentsBuilder uriBuilder) {
        PagamentoDTO pagamentoSalvo = pagamentoService.salvar(pagamento);
        URI uri = uriBuilder.path("/api/pagamentos/{id}")
                .buildAndExpand(pagamentoSalvo.getIdPagamento())
                .toUri();
        return ResponseEntity.created(uri).body(pagamentoSalvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pagamento", description = "Atualiza os dados de um pagamento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<PagamentoDTO> atualizar(
            @Parameter(description = "ID do pagamento") @PathVariable Long id,
            @Valid @RequestBody PagamentoDTO pagamento) {
        PagamentoDTO pagamentoAtualizado = pagamentoService.atualizar(id, pagamento);
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pagamento", description = "Remove um pagamento do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pagamento deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do pagamento") @PathVariable Long id) {
        pagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assinatura/{idAssinatura}")
    @Operation(summary = "Buscar pagamentos por assinatura", description = "Retorna todos os pagamentos de uma assinatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamentos encontrados"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    public ResponseEntity<List<PagamentoDTO>> buscarPorAssinatura(
            @Parameter(description = "ID da assinatura") @PathVariable Long idAssinatura) {
        List<PagamentoDTO> pagamentos = pagamentoService.buscarPorAssinatura(idAssinatura);
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Buscar pagamentos por período", description = "Retorna pagamentos realizados em um período específico")
    @ApiResponse(responseCode = "200", description = "Pagamentos encontrados")
    public ResponseEntity<List<PagamentoDTO>> buscarPorPeriodo(
            @Parameter(description = "Data início (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @Parameter(description = "Data fim (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim) {
        List<PagamentoDTO> pagamentos = pagamentoService.buscarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/assinatura/{idAssinatura}/periodo")
    @Operation(summary = "Buscar pagamentos por assinatura e período", description = "Retorna pagamentos de uma assinatura em período específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamentos encontrados"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    public ResponseEntity<List<PagamentoDTO>> buscarPorAssinaturaEPeriodo(
            @Parameter(description = "ID da assinatura") @PathVariable Long idAssinatura,
            @Parameter(description = "Data início (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @Parameter(description = "Data fim (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim) {
        List<PagamentoDTO> pagamentos = pagamentoService.buscarPorAssinaturaEPeriodo(idAssinatura, dataInicio, dataFim);
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/assinatura/{idAssinatura}/total")
    @Operation(summary = "Calcular total pago por assinatura", description = "Retorna o valor total já pago para uma assinatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    public ResponseEntity<BigDecimal> calcularTotalPagoPorAssinatura(
            @Parameter(description = "ID da assinatura") @PathVariable Long idAssinatura) {
        BigDecimal total = pagamentoService.calcularTotalPagoPorAssinatura(idAssinatura);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{idUsuario}/total")
    @Operation(summary = "Calcular total pago por usuário", description = "Retorna o valor total já pago por um usuário em todas suas assinaturas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<BigDecimal> calcularTotalPagoPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario) {
        BigDecimal total = pagamentoService.calcularTotalPagoPorUsuario(idUsuario);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/periodo/total")
    @Operation(summary = "Calcular total pago em período", description = "Retorna o valor total pago em um período específico")
    @ApiResponse(responseCode = "200", description = "Total calculado com sucesso")
    public ResponseEntity<BigDecimal> calcularTotalPagoPorPeriodo(
            @Parameter(description = "Data início (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @Parameter(description = "Data fim (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim) {
        BigDecimal total = pagamentoService.calcularTotalPagoPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{idUsuario}/periodo/total")
    @Operation(summary = "Calcular total pago por usuário em período", description = "Retorna o total pago por um usuário em período específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<BigDecimal> calcularTotalPagoPorUsuarioEPeriodo(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario,
            @Parameter(description = "Data início (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataInicio,
            @Parameter(description = "Data fim (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dataFim) {
        BigDecimal total = pagamentoService.calcularTotalPagoPorUsuarioEPeriodo(idUsuario, dataInicio, dataFim);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar pagamento automaticamente", description = "Registra um pagamento e atualiza automaticamente a próxima cobrança da assinatura")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagamento registrado e próxima cobrança atualizada"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<PagamentoDTO> registrarPagamento(
            @Parameter(description = "ID da assinatura") @RequestParam Long idAssinatura,
            @Parameter(description = "Valor pago") @RequestParam BigDecimal valorPago) {
        PagamentoDTO pagamento = pagamentoService.registrarPagamento(idAssinatura, valorPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }
}
