package org.example.restauranteapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El email no puede estar vacío")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "El nombre de usuario no puede estar vacío")
    @Column(unique = true)
    private String username;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    private String password;

    // Relación uno a uno con la entidad Cliente
    @OneToOne
    @JoinColumn(name = "cliente_id",referencedColumnName = "id") // Clave foránea que enlaza con la tabla Cliente
    private Cliente cliente;

    // Roles o permisos (Authorities)
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER) // Indica que la lista de authorities será cargada de inmediato
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> authorities = new ArrayList<>();

    // Métodos de UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Definir la lógica para expiración si lo deseas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Lógica para bloquear la cuenta si es necesario
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Si deseas controlar la expiración de credenciales
    }

    @Override
    public boolean isEnabled() {
        return true; // Si deseas controlar la habilitación del usuario
    }
}
