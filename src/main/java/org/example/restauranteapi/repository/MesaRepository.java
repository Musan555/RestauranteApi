package org.example.restauranteapi.repository;

import org.example.restauranteapi.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    public Mesa findByNumeroMesa(String numeroMesa);
}
