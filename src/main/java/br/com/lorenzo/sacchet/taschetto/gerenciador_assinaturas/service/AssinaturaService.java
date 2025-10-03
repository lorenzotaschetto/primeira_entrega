package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.*;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.AssinaturaDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.AssinaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssinaturaService {

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private TagService tagService;

    @Autowired
    private MetodoPagamentoService metodoPagamentoService;

    public List<AssinaturaDTO> listarTodas() {
        return assinaturaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<AssinaturaDTO> buscarPorId(Long id) {
        return assinaturaRepository.findById(id)
                .map(this::convertToDTO);
    }

    public AssinaturaDTO salvar(AssinaturaDTO assinaturaDTO) {
        Assinatura assinatura = convertToEntity(assinaturaDTO);
        Assinatura assinaturaSalva = assinaturaRepository.save(assinatura);
        return convertToDTO(assinaturaSalva);
    }

    public AssinaturaDTO atualizar(Long id, AssinaturaDTO assinaturaDTO) {
        return assinaturaRepository.findById(id)
                .map(assinatura -> {
                    assinatura.setNome(assinaturaDTO.getNome());
                    assinatura.setValor(assinaturaDTO.getValor());
                    assinatura.setCicloPagamento(assinaturaDTO.getCicloPagamento());
                    assinatura.setProximaCobranca(assinaturaDTO.getProximaCobranca());
                    assinatura.setCategoria(categoriaService.buscarEntidadePorId(assinaturaDTO.getIdCategoria()));

                    if (assinaturaDTO.getIdMetodoPago() != null) {
                        assinatura.setMetodoPagamento(metodoPagamentoService.buscarEntidadePorId(assinaturaDTO.getIdMetodoPago()));
                    } else {
                        assinatura.setMetodoPagamento(null);
                    }

                    if (assinaturaDTO.getIdsTag() != null && !assinaturaDTO.getIdsTag().isEmpty()) {
                        assinatura.setTags(tagService.buscarEntidadesPorIds(assinaturaDTO.getIdsTag()));
                    }

                    Assinatura assinaturaAtualizada = assinaturaRepository.save(assinatura);
                    return convertToDTO(assinaturaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", id));
    }

    public void deletar(Long id) {
        if (!assinaturaRepository.existsById(id)) {
            throw new EntityNotFoundException("Assinatura", id);
        }
        assinaturaRepository.deleteById(id);
    }

    public List<AssinaturaDTO> buscarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        return assinaturaRepository.findByUsuario(usuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaDTO> buscarPorCategoria(Long idCategoria) {
        Categoria categoria = categoriaService.buscarEntidadePorId(idCategoria);
        return assinaturaRepository.findByCategoria(categoria).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaDTO> buscarPorUsuarioECategoria(Long idUsuario, Long idCategoria) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        Categoria categoria = categoriaService.buscarEntidadePorId(idCategoria);
        return assinaturaRepository.findByUsuarioAndCategoria(usuario, categoria).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaDTO> buscarPorProximaCobrancaAte(LocalDate data) {
        return assinaturaRepository.findByProximaCobrancaLessThanEqual(data).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaDTO> buscarAssinaturasVencendoHoje() {
        return buscarPorProximaCobrancaAte(LocalDate.now());
    }

    public List<AssinaturaDTO> buscarAssinaturasVencendoEm(int dias) {
        return buscarPorProximaCobrancaAte(LocalDate.now().plusDays(dias));
    }

    public BigDecimal calcularTotalMensalPorUsuario(Long idUsuario) {
        List<Assinatura> assinaturas = assinaturaRepository.findByUsuario(usuarioService.buscarEntidadePorId(idUsuario));
        return assinaturas.stream()
                .filter(a -> "MENSAL".equalsIgnoreCase(a.getCicloPagamento()))
                .map(Assinatura::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalAnualPorUsuario(Long idUsuario) {
        List<Assinatura> assinaturas = assinaturaRepository.findByUsuario(usuarioService.buscarEntidadePorId(idUsuario));
        return assinaturas.stream()
                .map(a -> {
                    if ("MENSAL".equalsIgnoreCase(a.getCicloPagamento())) {
                        return a.getValor().multiply(BigDecimal.valueOf(12));
                    } else if ("ANUAL".equalsIgnoreCase(a.getCicloPagamento())) {
                        return a.getValor();
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<AssinaturaDTO> buscarPorTag(Long idTag) {
        Tag tag = tagService.buscarEntidadePorId(idTag);
        return assinaturaRepository.findByTagsContaining(tag).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AssinaturaDTO adicionarTag(Long idAssinatura, Long idTag) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));
        Tag tag = tagService.buscarEntidadePorId(idTag);

        if (!assinatura.getTags().contains(tag)) {
            assinatura.getTags().add(tag);
            Assinatura assinaturaAtualizada = assinaturaRepository.save(assinatura);
            return convertToDTO(assinaturaAtualizada);
        }
        return convertToDTO(assinatura);
    }

    public AssinaturaDTO removerTag(Long idAssinatura, Long idTag) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));
        Tag tag = tagService.buscarEntidadePorId(idTag);

        assinatura.getTags().remove(tag);
        Assinatura assinaturaAtualizada = assinaturaRepository.save(assinatura);
        return convertToDTO(assinaturaAtualizada);
    }

    // Métodos de conversão
    private AssinaturaDTO convertToDTO(Assinatura assinatura) {
        AssinaturaDTO dto = new AssinaturaDTO();
        dto.setIdAssinatura(assinatura.getIdAssinatura());
        dto.setNome(assinatura.getNome());
        dto.setValor(assinatura.getValor());
        dto.setCicloPagamento(assinatura.getCicloPagamento());
        dto.setProximaCobranca(assinatura.getProximaCobranca());
        dto.setIdUsuario(assinatura.getUsuario().getIdUsuario());
        dto.setIdCategoria(assinatura.getCategoria().getIdCategoria());

        if (assinatura.getMetodoPagamento() != null) {
            dto.setIdMetodoPago(assinatura.getMetodoPagamento().getIdMetodoPago());
        }

        if (assinatura.getTags() != null && !assinatura.getTags().isEmpty()) {
            dto.setIdsTag(assinatura.getTags().stream()
                    .map(Tag::getIdTag)
                    .collect(Collectors.toList()));
        }

        // Dados para exibição
        dto.setNomeUsuario(assinatura.getUsuario().getNome());
        dto.setNomeCategoria(assinatura.getCategoria().getNome());

        if (assinatura.getMetodoPagamento() != null) {
            dto.setNomeMetodoPagamento(assinatura.getMetodoPagamento().getNomePersonalizado());
        }

        if (assinatura.getTags() != null && !assinatura.getTags().isEmpty()) {
            dto.setNomesTags(assinatura.getTags().stream()
                    .map(Tag::getNome)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private Assinatura convertToEntity(AssinaturaDTO dto) {
        Assinatura assinatura = new Assinatura();
        assinatura.setIdAssinatura(dto.getIdAssinatura());
        assinatura.setNome(dto.getNome());
        assinatura.setValor(dto.getValor());
        assinatura.setCicloPagamento(dto.getCicloPagamento());
        assinatura.setProximaCobranca(dto.getProximaCobranca());

        // Buscar relacionamentos
        assinatura.setUsuario(usuarioService.buscarEntidadePorId(dto.getIdUsuario()));
        assinatura.setCategoria(categoriaService.buscarEntidadePorId(dto.getIdCategoria()));

        if (dto.getIdMetodoPago() != null) {
            assinatura.setMetodoPagamento(metodoPagamentoService.buscarEntidadePorId(dto.getIdMetodoPago()));
        }

        if (dto.getIdsTag() != null && !dto.getIdsTag().isEmpty()) {
            assinatura.setTags(tagService.buscarEntidadesPorIds(dto.getIdsTag()));
        }

        return assinatura;
    }

    // Método auxiliar para outros services
    public Assinatura buscarEntidadePorId(Long id) {
        return assinaturaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", id));
    }

    // Método público para conversão - usado por outros services
    public AssinaturaDTO convertEntityToDTO(Assinatura assinatura) {
        return convertToDTO(assinatura);
    }

    // Método público para conversão - usado por outros services
    public Assinatura convertDTOToEntity(AssinaturaDTO dto) {
        return convertToEntity(dto);
    }
}
