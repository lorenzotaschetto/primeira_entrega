package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioChangePasswordDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioCreateDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.usuarioDTO.UsuarioUpdateDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.BusinessException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Perfil;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.UsuarioRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#id)")
    public UsuarioResponseDTO buscarPorId(@Param("id") Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));
        return convertToResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO registrar(UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setPerfil(Perfil.ROLE_USUARIO);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return convertToResponseDTO(usuarioSalvo);
    }

    @Transactional
    @PreAuthorize("@securityService.checarAcessoUsuario(#id)")
    public UsuarioResponseDTO atualizar(@Param("id") Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return convertToResponseDTO(usuarioAtualizado);
    }
    @Transactional
    @PreAuthorize("@securityService.checarAcessoUsuario(#id)")
    public void deletar(@Param("id") Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<UsuarioResponseDTO> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::convertToResponseDTO);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));
    }

    private UsuarioResponseDTO convertToResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        return dto;
    }

    @Transactional
    @PreAuthorize("@securityService.checarAcessoUsuario(#id)")
    public void alterarSenha(Long id, UsuarioChangePasswordDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenhaHash())) {
            throw new BusinessException("Senha atual incorreta.");
        }

        usuario.setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }
}
