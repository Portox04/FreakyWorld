package com.freakyworld.repository;

import com.freakyworld.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}