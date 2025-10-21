package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Tag;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.TagDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.TagRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityInUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<TagDTO> listarTodas() {
        return tagRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TagDTO buscarPorId(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com id: " + id));
        return convertToDTO(tag);
    }

    public TagDTO salvar(TagDTO tagDTO) {
        Tag tag = convertToEntity(tagDTO);
        Tag tagSalva = tagRepository.save(tag);
        return convertToDTO(tagSalva);
    }

    public TagDTO atualizar(Long id, TagDTO tagDTO) {
        return tagRepository.findById(id)
                .map(tag -> {
                    tag.setNome(tagDTO.getNome());
                    Tag tagAtualizada = tagRepository.save(tag);
                    return convertToDTO(tagAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Tag", id));
    }

    public void deletar(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag", id));

        if (tag.getAssinaturas() != null && !tag.getAssinaturas().isEmpty()) {
            throw new EntityInUseException("Tag que possui assinaturas vinculadas");
        }

        tagRepository.deleteById(id);
    }

    public TagDTO buscarPorNome(String nome) {
        Tag tag = tagRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Tag não encontrada com nome: " + nome));
        return convertToDTO(tag);
    }

    public boolean existePorNome(String nome) {
        return tagRepository.existsByNome(nome);
    }

    public List<TagDTO> buscarPorIds(List<Long> ids) {
        return tagRepository.findAllById(ids).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setIdTag(tag.getIdTag());
        dto.setNome(tag.getNome());
        return dto;
    }

    private Tag convertToEntity(TagDTO dto) {
        Tag tag = new Tag();
        tag.setIdTag(dto.getIdTag());
        tag.setNome(dto.getNome());
        return tag;
    }

    public Tag buscarEntidadePorId(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag", id));
    }

    public List<Tag> buscarEntidadesPorIds(List<Long> ids) {
        return tagRepository.findAllById(ids);
    }
}
