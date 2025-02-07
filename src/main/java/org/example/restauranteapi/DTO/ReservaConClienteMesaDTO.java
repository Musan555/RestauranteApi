package org.example.restauranteapi.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservaConClienteMesaDTO {
    private String nombreCliente; // Nombre del cliente
    private String emailCliente; // Email del cliente
    private String telefonoCliente; // Teléfono del cliente
    private String fechaReserva; // Fecha de la reserva
    private String horaReserva; // Hora de la reserva
    private int numeroPersonas; // Número de personas en la reserva
    private String numeroMesa; // Número de la mesa
    private String descripcion; // Descripción de la mesa
}