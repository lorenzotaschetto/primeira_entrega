package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Categoria;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.dto.CategoriaDTO;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.repository.CategoriaRepository;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityNotFoundException;
import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.EntityInUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::convertToDTO);
    }

    public CategoriaDTO salvar(CategoriaDTO categoriaDTO) {
        Categoria categoria = convertToEntity(categoriaDTO);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return convertToDTO(categoriaSalva);
    }

    public CategoriaDTO atualizar(Long id, CategoriaDTO categoriaDTO) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNome(categoriaDTO.getNome());
                    Categoria categoriaAtualizada = categoriaRepository.save(categoria);
                    return convertToDTO(categoriaAtualizada);
                })
                .orElseThrow(() -> new EntityNotFoundException("Categoria", id));
    }

    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria", id));

        if (categoria.getAssinaturas() != null && !categoria.getAssinaturas().isEmpty()) {
            throw new EntityInUseException("Categoria que possui assinaturas vinculadas");
        }

        categoriaRepository.deleteById(id);
    }

    public Optional<CategoriaDTO> buscarPorNome(String nome) {
        return categoriaRepository.findByNome(nome)
                .map(this::convertToDTO);
    }

    public boolean existePorNome(String nome) {
        return categoriaRepository.existsByNome(nome);
    }

    // Métodos de conversão
    private CategoriaDTO convertToDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNome(categoria.getNome());
        return dto;
    }

    private Categoria convertToEntity(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(dto.getIdCategoria());
        categoria.setNome(dto.getNome());
        return categoria;
    }

    // Método auxiliar para outros services
    public Categoria buscarEntidadePorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria", id));
    }
}
