package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.controller;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.metodoPagamentoDTO.MetodoPagamentoRequestDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.metodoPagamentoDTO.MetodoPagamentoResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service.MetodoPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/metodos-pagamento")
@Tag(name = "Métodos de Pagamento", description = "Gerenciamento de métodos de pagamento dos usuários")
public class MetodoPagamentoController {

    @Autowired
    private MetodoPagamentoService metodoPagamentoService;

    @GetMapping
    @Operation(summary = "Listar todos os métodos de pagamento", description = "Retorna uma lista com todos os métodos de pagamento cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de métodos de pagamento retornada com sucesso")
    public ResponseEntity<List<MetodoPagamentoResponseDTO>>  listarTodos() {
        List<MetodoPagamentoResponseDTO> metodos = metodoPagamentoService.listarTodos();
        return ResponseEntity.ok(metodos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar método de pagamento por ID", description = "Retorna um método de pagamento específico pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Método de pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Método de pagamento não encontrado")
    })

    @PreAuthorize("@securityService.checarPosseMetodoPagamento(#id)")
    public ResponseEntity<MetodoPagamentoResponseDTO> buscarPorId(
            @Parameter(description = "ID do método de pagamento") @PathVariable Long id) {

        MetodoPagamentoResponseDTO metodo = metodoPagamentoService.buscarPorId(id);

        return ResponseEntity.ok(metodo);
    }

    @PostMapping
    @Operation(summary = "Criar novo método de pagamento", description = "Cria um novo método de pagamento no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Método de pagamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<MetodoPagamentoResponseDTO> criar(@Valid @RequestBody MetodoPagamentoRequestDTO metodoPagamento, UriComponentsBuilder uriBuilder) {
        MetodoPagamentoResponseDTO metodoSalvo = metodoPagamentoService.salvar(metodoPagamento);
        URI uri = uriBuilder.path("/api/metodos-pagamento/{id}")
                .buildAndExpand(metodoSalvo.getIdMetodoPago())
                .toUri();
        return ResponseEntity.created(uri).body(metodoSalvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar método de pagamento", description = "Atualiza os dados de um método de pagamento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Método de pagamento atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Método de pagamento não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<MetodoPagamentoResponseDTO> atualizar(
            @Parameter(description = "ID do método de pagamento") @PathVariable Long id,
            @Valid @RequestBody MetodoPagamentoRequestDTO metodoPagamento) {
        MetodoPagamentoResponseDTO metodoAtualizado = metodoPagamentoService.atualizar(id, metodoPagamento);
        return ResponseEntity.ok(metodoAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar método de pagamento", description = "Remove um método de pagamento do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Método de pagamento deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Método de pagamento não encontrado"),
        @ApiResponse(responseCode = "409", description = "Método de pagamento em uso não pode ser deletado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do método de pagamento") @PathVariable Long id) {
        metodoPagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Buscar métodos de pagamento por usuário", description = "Retorna todos os métodos de pagamento de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métodos de pagamento encontrados"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<MetodoPagamentoResponseDTO>> buscarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario) {
        List<MetodoPagamentoResponseDTO> metodos = metodoPagamentoService.buscarPorUsuario(idUsuario);
        return ResponseEntity.ok(metodos);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar métodos de pagamento por tipo", description = "Retorna todos os métodos de pagamento de um tipo específico")
    @ApiResponse(responseCode = "200", description = "Métodos de pagamento encontrados")
    public ResponseEntity<List<MetodoPagamentoResponseDTO>> buscarPorTipo(
            @Parameter(description = "Tipo do método de pagamento") @PathVariable String tipo) {
        List<MetodoPagamentoResponseDTO> metodos = metodoPagamentoService.buscarMeusPorTipo(tipo);
        return ResponseEntity.ok(metodos);
    }

    @GetMapping("/usuario/{idUsuario}/tipo/{tipo}")
    @Operation(summary = "Buscar métodos por usuário e tipo", description = "Retorna métodos de pagamento de um usuário filtrados por tipo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métodos de pagamento encontrados"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<MetodoPagamentoResponseDTO>> buscarPorUsuarioETipo(
            @Parameter(description = "ID do usuário") @PathVariable Long idUsuario,
            @Parameter(description = "Tipo do método de pagamento") @PathVariable String tipo) {
        List<MetodoPagamentoResponseDTO> metodos = metodoPagamentoService.buscarPorUsuarioETipo(idUsuario, tipo);
        return ResponseEntity.ok(metodos);
    }
}
