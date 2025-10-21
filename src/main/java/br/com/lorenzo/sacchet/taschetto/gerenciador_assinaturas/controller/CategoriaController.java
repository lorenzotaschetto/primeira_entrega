package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.controller;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.CategoriaDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Gerenciamento de categorias de assinaturas")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias", description = "Retorna uma lista com todas as categorias cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<CategoriaDTO> buscarPorId(
            @Parameter(description = "ID da categoria") @PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.buscarPorId(id);

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<CategoriaDTO> criar(@Valid @RequestBody CategoriaDTO categoria, UriComponentsBuilder uriBuilder) {
        CategoriaDTO categoriaSalva = categoriaService.salvar(categoria);
        URI uri = uriBuilder.path("/api/categorias/{id}")
                .buildAndExpand(categoriaSalva.getIdCategoria())
                .toUri();
        return ResponseEntity.created(uri).body(categoriaSalva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<CategoriaDTO> atualizar(
            @Parameter(description = "ID da categoria") @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO categoria) {
        CategoriaDTO categoriaAtualizada = categoriaService.atualizar(id, categoria);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria", description = "Remove uma categoria do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "409", description = "Categoria em uso não pode ser deletada")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da categoria") @PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar categoria por nome", description = "Retorna uma categoria pelo nome")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    public ResponseEntity<CategoriaDTO> buscarPorNome(
            @Parameter(description = "Nome da categoria") @PathVariable String nome) {
        CategoriaDTO categoria = categoriaService.buscarPorNome(nome);

        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/existe/{nome}")
    @Operation(summary = "Verificar se categoria existe", description = "Verifica se já existe uma categoria com o nome informado")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    public ResponseEntity<Boolean> existePorNome(
            @Parameter(description = "Nome para verificação") @PathVariable String nome) {
        boolean existe = categoriaService.existePorNome(nome);
        return ResponseEntity.ok(existe);
    }
}
