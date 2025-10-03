package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.controller;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.TagDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
@Tag(name = "Tags", description = "Gerenciamento de tags para organização das assinaturas")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    @Operation(summary = "Listar todas as tags", description = "Retorna uma lista com todas as tags cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de tags retornada com sucesso")
    public ResponseEntity<List<TagDTO>> listarTodas() {
        List<TagDTO> tags = tagService.listarTodas();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tag por ID", description = "Retorna uma tag específica pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag encontrada"),
        @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    public ResponseEntity<TagDTO> buscarPorId(
            @Parameter(description = "ID da tag") @PathVariable Long id) {
        Optional<TagDTO> tag = tagService.buscarPorId(id);
        return tag.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova tag", description = "Cria uma nova tag no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tag criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<TagDTO> criar(@Valid @RequestBody TagDTO tag, UriComponentsBuilder uriBuilder) {
        TagDTO tagSalva = tagService.salvar(tag);
        URI uri = uriBuilder.path("/api/tags/{id}")
                .buildAndExpand(tagSalva.getIdTag())
                .toUri();
        return ResponseEntity.created(uri).body(tagSalva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tag", description = "Atualiza os dados de uma tag existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Tag não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<TagDTO> atualizar(
            @Parameter(description = "ID da tag") @PathVariable Long id,
            @Valid @RequestBody TagDTO tag) {
        TagDTO tagAtualizada = tagService.atualizar(id, tag);
        return ResponseEntity.ok(tagAtualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar tag", description = "Remove uma tag do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tag deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Tag não encontrada"),
        @ApiResponse(responseCode = "409", description = "Tag em uso não pode ser deletada")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da tag") @PathVariable Long id) {
        tagService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Buscar tag por nome", description = "Retorna uma tag pelo nome")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag encontrada"),
        @ApiResponse(responseCode = "404", description = "Tag não encontrada")
    })
    public ResponseEntity<TagDTO> buscarPorNome(
            @Parameter(description = "Nome da tag") @PathVariable String nome) {
        Optional<TagDTO> tag = tagService.buscarPorNome(nome);
        return tag.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/buscar-por-ids")
    @Operation(summary = "Buscar tags por IDs", description = "Retorna uma lista de tags pelos IDs fornecidos")
    @ApiResponse(responseCode = "200", description = "Tags encontradas")
    public ResponseEntity<List<TagDTO>> buscarPorIds(
            @Parameter(description = "Lista de IDs das tags") @RequestBody List<Long> ids) {
        List<TagDTO> tags = tagService.buscarPorIds(ids);
        return ResponseEntity.ok(tags);
    }
}
