package org.example.restauranteapi.repository;

import org.example.restauranteapi.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Consulta personalizada para obtener reservas de una fecha específica
    @Query("SELECT r FROM Reserva r WHERE r.fecha = :fecha")
    List<Reserva> findReservasByFecha(@Param("fecha") LocalDate fecha);


    // Verificar si ya existe una reserva en la misma mesa (por número), fecha y hora
    boolean existsByMesaNumeroMesaAndFechaAndHora(String numeroMesa, LocalDate fecha, LocalTime hora);
}
