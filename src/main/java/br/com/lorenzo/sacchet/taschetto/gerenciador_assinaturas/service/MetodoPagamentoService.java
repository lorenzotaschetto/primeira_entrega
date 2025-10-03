package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.MetodoPagamento;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.MetodoPagamentoDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.MetodoPagamentoRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityInUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MetodoPagamentoService {

    @Autowired
    private MetodoPagamentoRepository metodoPagamentoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<MetodoPagamentoDTO> listarTodos() {
        return metodoPagamentoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MetodoPagamentoDTO> buscarPorId(Long id) {
        return metodoPagamentoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public MetodoPagamentoDTO salvar(MetodoPagamentoDTO metodoPagamentoDTO) {
        MetodoPagamento metodoPagamento = convertToEntity(metodoPagamentoDTO);
        MetodoPagamento metodoSalvo = metodoPagamentoRepository.save(metodoPagamento);
        return convertToDTO(metodoSalvo);
    }

    public MetodoPagamentoDTO atualizar(Long id, MetodoPagamentoDTO metodoPagamentoDTO) {
        return metodoPagamentoRepository.findById(id)
                .map(metodoPagamento -> {
                    metodoPagamento.setNomePersonalizado(metodoPagamentoDTO.getNomePersonalizado());
                    metodoPagamento.setTipo(metodoPagamentoDTO.getTipo());
                    metodoPagamento.setInfoAdicional(metodoPagamentoDTO.getInfoAdicional());
                    MetodoPagamento metodoAtualizado = metodoPagamentoRepository.save(metodoPagamento);
                    return convertToDTO(metodoAtualizado);
                })
                .orElseThrow(() -> new EntityNotFoundException("Método de pagamento", id));
    }

    public void deletar(Long id) {
        MetodoPagamento metodoPagamento = metodoPagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Método de pagamento", id));

        if (metodoPagamento.getAssinaturas() != null && !metodoPagamento.getAssinaturas().isEmpty()) {
            throw new EntityInUseException("Método de pagamento que possui assinaturas vinculadas");
        }

        metodoPagamentoRepository.deleteById(id);
    }

    public List<MetodoPagamentoDTO> buscarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        return metodoPagamentoRepository.findByUsuario(usuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MetodoPagamentoDTO> buscarPorTipo(String tipo) {
        return metodoPagamentoRepository.findByTipo(tipo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MetodoPagamentoDTO> buscarPorUsuarioETipo(Long idUsuario, String tipo) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        return metodoPagamentoRepository.findByUsuarioAndTipo(usuario, tipo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Métodos de conversão
    private MetodoPagamentoDTO convertToDTO(MetodoPagamento metodoPagamento) {
        MetodoPagamentoDTO dto = new MetodoPagamentoDTO();
        dto.setIdMetodoPago(metodoPagamento.getIdMetodoPago());
        dto.setNomePersonalizado(metodoPagamento.getNomePersonalizado());
        dto.setTipo(metodoPagamento.getTipo());
        dto.setInfoAdicional(metodoPagamento.getInfoAdicional());
        dto.setIdUsuario(metodoPagamento.getUsuario().getIdUsuario());
        return dto;
    }

    private MetodoPagamento convertToEntity(MetodoPagamentoDTO dto) {
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setIdMetodoPago(dto.getIdMetodoPago());
        metodoPagamento.setNomePersonalizado(dto.getNomePersonalizado());
        metodoPagamento.setTipo(dto.getTipo());
        metodoPagamento.setInfoAdicional(dto.getInfoAdicional());

        // Buscar o usuário pela entidade
        Usuario usuario = usuarioService.buscarEntidadePorId(dto.getIdUsuario());
        metodoPagamento.setUsuario(usuario);

        return metodoPagamento;
    }

    // Método auxiliar para outros services
    public MetodoPagamento buscarEntidadePorId(Long id) {
        return metodoPagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Método de pagamento", id));
    }
}
