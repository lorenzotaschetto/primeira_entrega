package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.assinaturaDTO.AssinaturaRequestDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.assinaturaDTO.AssinaturaResponseDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.BusinessException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.infra.security.SecurityHelper;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.*;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.AssinaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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

    @Autowired
    private SecurityHelper securityHelper;

    public List<AssinaturaResponseDTO> listarTodas() {
        return assinaturaRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("@securityService.checarPosseAssinatura(#id)")
    public AssinaturaResponseDTO buscarPorId(@Param("id") Long id) {
        Assinatura assinatura = assinaturaRepository.findById(id).get();
        return convertToResponseDTO(assinatura);
    }

    @Transactional
    public AssinaturaResponseDTO salvar(AssinaturaRequestDTO dto) {

        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();

        Categoria categoria = categoriaService.buscarEntidadePorId(dto.getIdCategoria());

        Assinatura novaAssinatura = new Assinatura();

        novaAssinatura.setNome(dto.getNome());
        novaAssinatura.setValor(dto.getValor());
        novaAssinatura.setCicloPagamento(dto.getCicloPagamento());
        novaAssinatura.setProximaCobranca(dto.getProximaCobranca());

        novaAssinatura.setUsuario(usuarioLogado);
        novaAssinatura.setCategoria(categoria);

        if (dto.getIdMetodoPago() != null) {
            MetodoPagamento mp = metodoPagamentoService.buscarEntidadePorId(dto.getIdMetodoPago());

            if (!securityHelper.isAdmin(usuarioLogado) && !mp.getUsuario().getIdUsuario().equals(usuarioLogado.getIdUsuario())) {
                throw new BusinessException("O método de pagamento selecionado (ID: " + dto.getIdMetodoPago() + ") não pertence ao usuário autenticado.");
            }
            novaAssinatura.setMetodoPagamento(mp);
        }

        if (dto.getIdsTag() != null && !dto.getIdsTag().isEmpty()) {
            List<Tag> tags = tagService.buscarEntidadesPorIds(dto.getIdsTag());
            novaAssinatura.setTags(tags);
        } else {
            novaAssinatura.setTags(Collections.emptyList());
        }

        Assinatura assinaturaSalva = assinaturaRepository.save(novaAssinatura);

        return convertToResponseDTO(assinaturaSalva);
    }

    @PreAuthorize("@securityService.checarPosseAssinatura(#id)")
    public AssinaturaResponseDTO atualizar(@Param("id") Long id, AssinaturaRequestDTO assinaturaDTO) {
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
                    return convertToResponseDTO(assinaturaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", id));
    }

    @Transactional
    @PreAuthorize("@securityService.checarPosseAssinatura(#id)")
    public void deletar(@Param("id") Long id) {

        assinaturaRepository.deleteById(id);
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#idUsuario)")
    public List<AssinaturaResponseDTO> buscarPorUsuario(@Param("idUsuario") Long idUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        return assinaturaRepository.findByUsuario(usuario).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaResponseDTO> buscarPorUsuario() {
        Usuario usuario = securityHelper.getUsuarioAutenticado();
        return assinaturaRepository.findByUsuario(usuario).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaResponseDTO> buscarPorCategoria(Long idCategoria) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        Categoria categoria = categoriaService.buscarEntidadePorId(idCategoria);
        return assinaturaRepository.findByCategoriaAndUsuario(categoria, usuarioLogado).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#idUsuario)")
    public List<AssinaturaResponseDTO> buscarPorUsuarioECategoria(@Param("idUsuario") Long idUsuario, Long idCategoria) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        Categoria categoria = categoriaService.buscarEntidadePorId(idCategoria);
        return assinaturaRepository.findByUsuarioAndCategoria(usuario, categoria).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaResponseDTO> buscarPorProximaCobrancaAte(LocalDate data) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        return assinaturaRepository.findByUsuarioAndProximaCobrancaLessThanEqual(usuarioLogado, data).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AssinaturaResponseDTO> buscarAssinaturasVencendoHoje() {
        return buscarPorProximaCobrancaAte(LocalDate.now());
    }

    public List<AssinaturaResponseDTO> buscarAssinaturasVencendoEm(int dias) {
        return buscarPorProximaCobrancaAte(LocalDate.now().plusDays(dias));
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#idUsuario)")
    public BigDecimal calcularTotalMensalPorUsuario(@Param("idUsuario") Long idUsuario) {
        List<Assinatura> assinaturas = assinaturaRepository.findByUsuario(usuarioService.buscarEntidadePorId(idUsuario));
        return assinaturas.stream()
                .filter(a -> "MENSAL".equalsIgnoreCase(a.getCicloPagamento()))
                .map(Assinatura::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PreAuthorize("@securityService.checarAcessoUsuario(#idUsuario)")
    public BigDecimal calcularTotalAnualPorUsuario(@Param("idUsuario") Long idUsuario) {
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

    public List<AssinaturaResponseDTO> buscarPorTag(Long idTag) {
        Usuario usuarioLogado = securityHelper.getUsuarioAutenticado();
        Tag tag = tagService.buscarEntidadePorId(idTag);
        return assinaturaRepository.findByUsuarioAndTagsContaining(usuarioLogado, tag).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@securityService.checarPosseAssinatura(#idAssinatura)")
    public AssinaturaResponseDTO adicionarTag(@Param("idAssinatura") Long idAssinatura, Long idTag) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));
        Tag tag = tagService.buscarEntidadePorId(idTag);

        if (!assinatura.getTags().contains(tag)) {
            assinatura.getTags().add(tag);
            Assinatura assinaturaAtualizada = assinaturaRepository.save(assinatura);
            return convertToResponseDTO(assinaturaAtualizada);
        }
        return convertToResponseDTO(assinatura);
    }

    @Transactional
    @PreAuthorize("@securityService.checarPosseAssinatura(#idAssinatura)")
    public AssinaturaResponseDTO removerTag(@Param("idAssinatura") Long idAssinatura, Long idTag) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura", idAssinatura));
        Tag tag = tagService.buscarEntidadePorId(idTag);

        assinatura.getTags().remove(tag);
        Assinatura assinaturaAtualizada = assinaturaRepository.save(assinatura);
        return convertToResponseDTO(assinaturaAtualizada);
    }

    private AssinaturaResponseDTO convertToResponseDTO(Assinatura assinatura) {
        AssinaturaResponseDTO dto = new AssinaturaResponseDTO();
        dto.setIdAssinatura(assinatura.getIdAssinatura());
        dto.setNome(assinatura.getNome());
        dto.setValor(assinatura.getValor());
        dto.setCicloPagamento(assinatura.getCicloPagamento());
        dto.setProximaCobranca(assinatura.getProximaCobranca());

        if (assinatura.getUsuario() != null) {
            dto.setIdUsuario(assinatura.getUsuario().getIdUsuario());
            dto.setNomeUsuario(assinatura.getUsuario().getNome());
        }

        if (assinatura.getCategoria() != null) {
            dto.setIdCategoria(assinatura.getCategoria().getIdCategoria());
            dto.setNomeCategoria(assinatura.getCategoria().getNome());
        }

        if (assinatura.getMetodoPagamento() != null) {
            dto.setIdMetodoPago(assinatura.getMetodoPagamento().getIdMetodoPago());
            dto.setNomeMetodoPagamento(assinatura.getMetodoPagamento().getNomePersonalizado());
        }

        if (assinatura.getTags() != null && !assinatura.getTags().isEmpty()) {
            dto.setIdsTag(assinatura.getTags().stream().map(Tag::getIdTag).collect(Collectors.toList()));
            dto.setNomesTags(assinatura.getTags().stream().map(Tag::getNome).collect(Collectors.toList()));
        } else {
            dto.setIdsTag(Collections.emptyList());
            dto.setNomesTags(Collections.emptyList());
        }

        return dto;
    }
}
