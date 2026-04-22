package com.freakyworld.repository;

import com.freakyworld.domain.Cupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuponRepository extends JpaRepository<Cupon, Long> {

    Optional<Cupon> findByCodigoIgnoreCaseAndActivoTrue(String codigo);
}