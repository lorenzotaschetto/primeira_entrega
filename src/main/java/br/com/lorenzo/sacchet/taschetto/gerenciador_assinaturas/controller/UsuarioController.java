package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.controller;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioChangePasswordDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioCreateDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioUpdateDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service.UsuarioService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registrar")
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioCreateDTO usuario, UriComponentsBuilder uriBuilder) {
        UsuarioResponseDTO usuarioSalvo = usuarioService.registrar(usuario);
        URI uri = uriBuilder.path("/api/usuarios/{id}")
                .buildAndExpand(usuarioSalvo.getIdUsuario())
                .toUri();
        return ResponseEntity.created(uri).body(usuarioSalvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO usuario) {
        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizar(id, usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário pelo endereço de email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(
            @Parameter(description = "Email do usuário") @PathVariable String email) {
        Optional<UsuarioResponseDTO> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/existe/{email}")
    @Operation(summary = "Verificar se email existe", description = "Verifica se já existe um usuário com o email informado")
    @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    public ResponseEntity<Boolean> existePorEmail(
            @Parameter(description = "Email para verificação") @PathVariable String email) {
        boolean existe = usuarioService.existePorEmail(email);
        return ResponseEntity.ok(existe);
    }

    @PatchMapping("/{id}/alterar-senha")
    @Operation(summary = "Alterar senha do usuário")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioChangePasswordDTO dto) {

        usuarioService.alterarSenha(id, dto);
        return ResponseEntity.noContent().build();
    }
}
