package org.example.restauranteapi.controller;

import org.example.restauranteapi.DTO.ReservaConClienteMesaDTO;
import org.example.restauranteapi.entity.Reserva;
import org.example.restauranteapi.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReservaConClienteMesaController {
    @Autowired
    ReservaRepository reservaRepository;

    @GetMapping("/reservas/dia")
    public ResponseEntity<List<ReservaConClienteMesaDTO>> getReservasPorFecha(@RequestParam("fecha") String fecha) {
        LocalDate fechaReserva = LocalDate.parse(fecha);

        List<Reserva> reservas = reservaRepository.findReservasByFecha(fechaReserva);

        List<ReservaConClienteMesaDTO> reservasDTO = new ArrayList<>();
        for (Reserva reserva : reservas) {
            reservasDTO.add(
                    ReservaConClienteMesaDTO.builder()
                            .nombreCliente(reserva.getCliente().getNombre())
                            .emailCliente(reserva.getCliente().getEmail())
                            .telefonoCliente(reserva.getCliente().getTelefono())
                            .fechaReserva(reserva.getFecha().toString())
                            .horaReserva(reserva.getHora().toString())
                            .numeroPersonas(reserva.getNumeroPersonas())
                            .numeroMesa(reserva.getMesa().getNumeroMesa())
                            .descripcion(reserva.getMesa().getDescripcion())
                            .build()
            );
        }

        return ResponseEntity.ok(reservasDTO);
    }

}
