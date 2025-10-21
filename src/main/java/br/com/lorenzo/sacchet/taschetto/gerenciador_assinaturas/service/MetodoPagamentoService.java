package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.metodoPagamentoDTO.MetodoPagamentoRequestDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.metodoPagamentoDTO.MetodoPagamentoResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityInUseException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.infra.security.SecurityHelper;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.MetodoPagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.MetodoPagamentoRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetodoPagamentoService {

    @Autowired
    private MetodoPagamentoRepository metodoPagamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SecurityHelper securityHelper;

    public List<MetodoPagamentoResponseDTO> listarTodos() {
        return metodoPagamentoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MetodoPagamentoResponseDTO> listarMeusMetodosPagamento() {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        return metodoPagamentoRepository.findByUsuario(usuarioLogado).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    @PreAuthorize("@securityService.checarPosseMetodoPagamento(#id)")
    public MetodoPagamentoResponseDTO buscarPorId(@Param("id") Long id) {
        MetodoPagamento metodoPagamento = buscarEntidadePorId(id);
        return convertToResponseDTO(metodoPagamento);
    }

    @Transactional
    public MetodoPagamentoResponseDTO salvar(MetodoPagamentoRequestDTO dto) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        if (!securityHelper.isAdmin(usuarioLogado) && !dto.getIdUsuario().equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Você só pode criar métodos de pagamento para si mesmo.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuário", dto.getIdUsuario()));


        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setNomePersonalizado(dto.getNomePersonalizado());
        metodoPagamento.setTipo(dto.getTipo());
        metodoPagamento.setInfoAdicional(dto.getInfoAdicional());
        metodoPagamento.setUsuario(usuario);

        MetodoPagamento metodoSalvo = metodoPagamentoRepository.save(metodoPagamento);
        return convertToResponseDTO(metodoSalvo);
    }

    @Transactional
    @PreAuthorize("@securityService.checarPosseMetodoPagamento(#id)")
    public MetodoPagamentoResponseDTO atualizar(@Param("id") Long id, MetodoPagamentoRequestDTO dto) {
        MetodoPagamento metodoPagamento = buscarEntidadePorId(id);

        metodoPagamento.setNomePersonalizado(dto.getNomePersonalizado());
        metodoPagamento.setTipo(dto.getTipo());
        metodoPagamento.setInfoAdicional(dto.getInfoAdicional());

        MetodoPagamento metodoAtualizado = metodoPagamentoRepository.save(metodoPagamento);
        return convertToResponseDTO(metodoAtualizado);
    }

    @Transactional
    @PreAuthorize("@securityService.checarPosseMetodoPagamento(#id)")
    public void deletar(@Param("id") Long id) {
        MetodoPagamento metodoPagamento = buscarEntidadePorId(id);

        if (metodoPagamento.getAssinaturas() != null && !metodoPagamento.getAssinaturas().isEmpty()) {
            throw new EntityInUseException("Método de pagamento está vinculado a uma ou mais assinaturas.");
        }
        metodoPagamentoRepository.deleteById(id);
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#idUsuario)")
    public List<MetodoPagamentoResponseDTO> buscarPorUsuario(@Param("idUsuario") Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", idUsuario));
        return metodoPagamentoRepository.findByUsuario(usuario).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MetodoPagamentoResponseDTO> buscarMeusPorTipo(String tipo) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        return metodoPagamentoRepository.findByUsuarioAndTipo(usuarioLogado, tipo).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public MetodoPagamento buscarEntidadePorId(Long id) {
        return metodoPagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Método de pagamento", id));
    }

    private MetodoPagamentoResponseDTO convertToResponseDTO(MetodoPagamento metodoPagamento) {
        MetodoPagamentoResponseDTO dto = new MetodoPagamentoResponseDTO();
        dto.setIdMetodoPago(metodoPagamento.getIdMetodoPago());
        dto.setNomePersonalizado(metodoPagamento.getNomePersonalizado());
        dto.setTipo(metodoPagamento.getTipo());
        dto.setInfoAdicional(metodoPagamento.getInfoAdicional());
        return dto;
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#idUsuario)")
    public List<MetodoPagamentoResponseDTO> buscarPorUsuarioETipo(@Param("idUsuario") Long idUsuario, String tipo) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", idUsuario));

        List<MetodoPagamento> metodosPagamento = metodoPagamentoRepository.findByUsuarioAndTipo(usuario, tipo);

        return metodosPagamento.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}