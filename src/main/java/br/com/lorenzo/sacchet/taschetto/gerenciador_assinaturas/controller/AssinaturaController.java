package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.controller;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.assinaturaDTO.AssinaturaRequestDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.assinaturaDTO.AssinaturaResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service.AssinaturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assinaturas")
@Tag(name = "Assinaturas", description = "Gerenciamento de assinaturas do sistema")
public class AssinaturaController {

    @Autowired
    private AssinaturaService assinaturaService;

    @GetMapping
    @Operation(summary = "Listar todas as assinaturas", description = "Retorna uma lista com todas as assinaturas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de assinaturas retornada com sucesso")
    public ResponseEntity<List<AssinaturaResponseDTO>> listarTodas() {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.listarTodas();
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar assinatura por ID", description = "Retorna uma assinatura específica pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assinatura encontrada"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    public ResponseEntity<AssinaturaResponseDTO> buscarPorId(
            @Parameter(description = "ID da assinatura") @PathVariable Long id) {
        AssinaturaResponseDTO assinatura = assinaturaService.buscarPorId(id);

        return ResponseEntity.ok(assinatura);
    }

    @PostMapping
    @Operation(summary = "Criar nova assinatura", description = "Cria uma nova assinatura no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Assinatura criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<AssinaturaResponseDTO> criar(@Valid @RequestBody AssinaturaRequestDTO assinatura, UriComponentsBuilder uriBuilder) {
        AssinaturaResponseDTO assinaturaSalva = assinaturaService.salvar(assinatura);
        URI uri = uriBuilder.path("/api/assinaturas/{id}")
                .buildAndExpand(assinaturaSalva.getIdAssinatura())
                .toUri();
        return ResponseEntity.created(uri).body(assinaturaSalva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar assinatura", description = "Atualiza os dados de uma assinatura existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assinatura atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<AssinaturaResponseDTO> atualizar(
            @Parameter(description = "ID da assinatura") @PathVariable Long id,
            @Valid @RequestBody AssinaturaRequestDTO assinatura) {
        AssinaturaResponseDTO assinaturaAtualizada = assinaturaService.atualizar(id, assinatura);
        return ResponseEntity.ok(assinaturaAtualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar assinatura", description = "Remove uma assinatura do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Assinatura deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Assinatura não encontrada")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da assinatura") @PathVariable Long id) {
        assinaturaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Buscar assinaturas por usuário", description = "Retorna todas as assinaturas de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assinaturas encontradas"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario) {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarPorUsuario(idUsuario);
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/categoria/{idCategoria}")
    @Operation(summary = "Buscar assinaturas por categoria", description = "Retorna todas as assinaturas de uma categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assinaturas encontradas"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarPorCategoria(
            @Parameter(description = "ID da categoria") @PathVariable Long idCategoria) {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarPorCategoria(idCategoria);
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/usuario/{idUsuario}/categoria/{idCategoria}")
    @Operation(summary = "Buscar assinaturas por usuário e categoria", description = "Retorna assinaturas filtradas por usuário e categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assinaturas encontradas"),
        @ApiResponse(responseCode = "404", description = "Usuário ou categoria não encontrada")
    })
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarPorUsuarioECategoria(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario,
            @Parameter(description = "ID da categoria") @PathVariable Long idCategoria) {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarPorUsuarioECategoria(idUsuario, idCategoria);
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/vencendo-hoje")
    @Operation(summary = "Buscar assinaturas vencendo hoje", description = "Retorna assinaturas que vencem na data atual")
    @ApiResponse(responseCode = "200", description = "Assinaturas encontradas")
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarAssinaturasVencendoHoje() {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarAssinaturasVencendoHoje();
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/vencendo-em/{dias}")
    @Operation(summary = "Buscar assinaturas vencendo em X dias", description = "Retorna assinaturas que vencem até a quantidade de dias especificada")
    @ApiResponse(responseCode = "200", description = "Assinaturas encontradas")
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarAssinaturasVencendoEm(
            @Parameter(description = "Quantidade de dias") @PathVariable int dias) {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarAssinaturasVencendoEm(dias);
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/vencendo-ate/{data}")
    @Operation(summary = "Buscar assinaturas vencendo até data", description = "Retorna assinaturas que vencem até a data especificada")
    @ApiResponse(responseCode = "200", description = "Assinaturas encontradas")
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarPorProximaCobrancaAte(
            @Parameter(description = "Data limite (YYYY-MM-DD)")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarPorProximaCobrancaAte(data);
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/tag/{idTag}")
    @Operation(summary = "Buscar assinaturas por tag", description = "Retorna todas as assinaturas que possuem uma tag específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assinaturas encontradas"),
        @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    public ResponseEntity<List<AssinaturaResponseDTO>> buscarPorTag(
            @Parameter(description = "ID da tag") @PathVariable Long idTag) {
        List<AssinaturaResponseDTO> assinaturas = assinaturaService.buscarPorTag(idTag);
        return ResponseEntity.ok(assinaturas);
    }

    @GetMapping("/usuario/{idUsuario}/total-mensal")
    @Operation(summary = "Calcular total mensal do usuário", description = "Retorna o total mensal de assinaturas de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<BigDecimal> calcularTotalMensalPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario) {
        BigDecimal total = assinaturaService.calcularTotalMensalPorUsuario(idUsuario);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{idUsuario}/total-anual")
    @Operation(summary = "Calcular total anual do usuário", description = "Retorna o total anual projetado de assinaturas de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<BigDecimal> calcularTotalAnualPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario) {
        BigDecimal total = assinaturaService.calcularTotalAnualPorUsuario(idUsuario);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/{idAssinatura}/tags/{idTag}")
    @Operation(summary = "Adicionar tag à assinatura", description = "Adiciona uma tag a uma assinatura específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag adicionada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Assinatura ou tag não encontrada")
    })
    public ResponseEntity<AssinaturaResponseDTO> adicionarTag(
            @Parameter(description = "ID da assinatura") @PathVariable Long idAssinatura,
            @Parameter(description = "ID da tag") @PathVariable Long idTag) {
        AssinaturaResponseDTO assinatura = assinaturaService.adicionarTag(idAssinatura, idTag);
        return ResponseEntity.ok(assinatura);
    }

    @DeleteMapping("/{idAssinatura}/tags/{idTag}")
    @Operation(summary = "Remover tag da assinatura", description = "Remove uma tag de uma assinatura específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Assinatura ou tag não encontrada")
    })
    public ResponseEntity<AssinaturaResponseDTO> removerTag(
            @Parameter(description = "ID da assinatura") @PathVariable Long idAssinatura,
            @Parameter(description = "ID da tag") @PathVariable Long idTag) {
        AssinaturaResponseDTO assinatura = assinaturaService.removerTag(idAssinatura, idTag);
        return ResponseEntity.ok(assinatura);
    }
}
