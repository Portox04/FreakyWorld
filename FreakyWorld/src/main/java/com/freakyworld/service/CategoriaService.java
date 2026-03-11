package com.freakyworld.service;

import com.freakyworld.domain.Categoria;
import com.freakyworld.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Categoria categoria) {
        Categoria categoriaActual = categoriaRepository.findById(categoria.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        categoriaActual.setNombre(categoria.getNombre());
        categoriaActual.setDescripcion(categoria.getDescripcion());

        return categoriaRepository.save(categoriaActual);
    }

    @Transactional
    public void eliminar(Long idCategoria) {
        categoriaRepository.deleteById(idCategoria);
    }
}
