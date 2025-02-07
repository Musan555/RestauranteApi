package org.example.restauranteapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="mesas")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroMesa;
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Relación con Reserva
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reserva> reservas;
}
