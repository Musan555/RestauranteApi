package org.example.restauranteapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="reservas" ,uniqueConstraints = {@UniqueConstraint(columnNames = {"mesa_id", "fecha", "hora"})})
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FutureOrPresent(message = "La fecha de la reserva no puede ser en el pasado")
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private int numeroPersonas;

    @ManyToOne(targetEntity = Cliente.class)
    private Cliente cliente;

    @ManyToOne(targetEntity = Mesa.class)
    private Mesa mesa;
}

