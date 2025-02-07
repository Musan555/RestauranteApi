package org.example.restauranteapi.repository;

import org.example.restauranteapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    public Cliente findByNombre(String nombre);
}
