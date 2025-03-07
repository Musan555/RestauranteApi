package org.example.restauranteapi.controller;

import jakarta.validation.Valid;
import org.example.restauranteapi.config.JwtTokenProvider;
import org.example.restauranteapi.entity.Cliente;
import org.example.restauranteapi.entity.Mesa;
import org.example.restauranteapi.entity.Reserva;
import org.example.restauranteapi.entity.UserEntity;
import org.example.restauranteapi.repository.ClienteRepository;
import org.example.restauranteapi.repository.MesaRepository;
import org.example.restauranteapi.repository.ReservaRepository;
import org.example.restauranteapi.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserEntityRepository userEntityRepository;

    // Obtener todas las reservas
    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> getListaReservas() {
        var reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    // Insertar una nueva reserva con verificación de disponibilidad
    @PostMapping("/reservas")
    public ResponseEntity<?> insertReserva(@RequestBody @Valid Reserva reserva,@RequestHeader("Authorization") String token) {
        String tokenSi = token.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromToken(tokenSi);
        Optional<UserEntity> userEntity = userEntityRepository.findByUsername(username);


        // Buscar cliente por email (puedes ajustar esto según tu lógica)
        Cliente cliente = clienteRepository.findByEmail(reserva.getCliente().getEmail());
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El cliente no existe.");
        }

        // Buscar mesa por número de mesa
        Mesa mesa = mesaRepository.findByNumeroMesa(reserva.getMesa().getNumeroMesa());
        if (mesa == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La mesa no existe.");
        }

        // Verificar disponibilidad de la mesa
        boolean mesaOcupada = reservaRepository.existsByMesaNumeroMesaAndFechaAndHora(
                reserva.getMesa().getNumeroMesa(), reserva.getFecha(), reserva.getHora());

        if (mesaOcupada) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La mesa ya está reservada para la fecha y hora seleccionadas.");
        }

        // Asignar cliente y mesa antes de guardar
        if (!cliente.getId().equals(userEntity.get().getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para crear");
        }
        reserva.setCliente(cliente);
        reserva.setMesa(mesa);

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
    public ResponseEntity<?> deleteReserva(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // Extraer el token sin "Bearer "
        String tokenSi = token.replace("Bearer ", "");
        // Obtener el username desde el token
        String username = tokenProvider.getUsernameFromToken(tokenSi);

        // Buscar el UserEntity correspondiente al username
        Optional<UserEntity> userEntity = userEntityRepository.findByUsername(username);

        // Verificar si el usuario existe
        if (userEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        // Buscar la reserva por su ID
        return reservaRepository.findById(id)
                .map(reserva -> {
                    // Verificar si la reserva pertenece al cliente del usuario autenticado
                    if (!reserva.getCliente().getId().equals(userEntity.get().getCliente().getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para eliminar");
                    }

                    // Eliminar la reserva
                    reservaRepository.delete(reserva);

                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}