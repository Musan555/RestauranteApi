package org.example.restauranteapi.controller;

import jakarta.validation.Valid;
import org.example.restauranteapi.entity.Reserva;
import org.example.restauranteapi.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    // Obtener todas las reservas
    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> getListaReservas() {
        var reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    // Insertar una nueva reserva con verificación de disponibilidad
    @PostMapping("/reservas")
    public ResponseEntity<?> insertReserva(@RequestBody @Valid Reserva reserva) {
        boolean mesaOcupada = reservaRepository.existsByMesaNumeroMesaAndFechaAndHora(
                reserva.getMesa().getNumeroMesa(), reserva.getFecha(), reserva.getHora());

        if (mesaOcupada) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La mesa ya está reservada para la fecha y hora seleccionadas.");
        }

        var reservaGuardada = reservaRepository.save(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaGuardada);
    }

    // Obtener una reserva por ID
    @GetMapping("/reservas/{id}")
    public ResponseEntity<Reserva> getReserva(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> ResponseEntity.ok().body(reserva))
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar una reserva
    @PutMapping("/reservas/{id}")
    public ResponseEntity<?> editReserva(@PathVariable Long id, @RequestBody @Valid Reserva nuevaReserva) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    boolean mesaOcupada = reservaRepository.existsByMesaNumeroMesaAndFechaAndHora(
                            nuevaReserva.getMesa().getNumeroMesa(), nuevaReserva.getFecha(), nuevaReserva.getHora());

                    if (mesaOcupada && !reserva.getMesa().getNumeroMesa().equals(nuevaReserva.getMesa().getNumeroMesa())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("La mesa ya está reservada para la fecha y hora seleccionadas.");
                    }

                    reserva.setFecha(nuevaReserva.getFecha());
                    reserva.setHora(nuevaReserva.getHora());
                    reserva.setNumeroPersonas(nuevaReserva.getNumeroPersonas());
                    reserva.setCliente(nuevaReserva.getCliente());
                    reserva.setMesa(nuevaReserva.getMesa());

                    return ResponseEntity.ok(reservaRepository.save(reserva));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una reserva
    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reservaRepository.delete(reserva);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}