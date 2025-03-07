package org.example.restauranteapi.controller;

import org.example.restauranteapi.DTO.LoginRequestDTO;
import org.example.restauranteapi.DTO.LoginResponseDTO;
import org.example.restauranteapi.DTO.UserRegistrerDTO;
import org.example.restauranteapi.config.JwtTokenProvider;
import org.example.restauranteapi.entity.Cliente;
import org.example.restauranteapi.entity.UserEntity;
import org.example.restauranteapi.repository.ClienteRepository;  // Importa el repositorio de Cliente
import org.example.restauranteapi.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private UserEntityRepository userRepository;

    @Autowired
    private ClienteRepository clienteRepository;  // Añadimos el repositorio Cliente

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/auth/register")
    public ResponseEntity<UserEntity> save(@RequestBody UserRegistrerDTO userDTO) {

        // Crear el Cliente asociado al UserEntity (relación uno a uno)
        Cliente cliente1 = new Cliente();
        cliente1.setNombre(userDTO.getNombre());  // Usamos el nombre real del cliente
        cliente1.setEmail(userDTO.getEmail());
        cliente1.setTelefono(userDTO.getTelefono());  // Asegúrate de que el teléfono no sea null


        // Guardar el Cliente en la base de datos
        this.clienteRepository.save(cliente1);


        // Crear el UserEntity
        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .cliente(cliente1)
                .authorities(List.of("ROLE_USER", "ROLE_ADMIN"))
                .build();

        // Guardar el UserEntity en la base de datos
        userEntity = this.userRepository.save(userEntity);



        return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);
    }



    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            // Validamos al usuario en Spring (hacemos login manualmente)
            UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication auth = authenticationManager.authenticate(userPassAuthToken);  // Valida el usuario y devuelve un objeto Authentication con sus datos

            // Obtenemos el UserEntity del usuario logueado
            UserEntity user = (UserEntity) auth.getPrincipal();

            // Generamos un token con los datos del usuario (la clase tokenProvider ha sido creada para manejarlo)
            String token = this.tokenProvider.generateToken(auth);

            // Devolvemos un código 200 con el username y token JWT
            return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), token));
        } catch (Exception e) {  // Si el usuario no es válido, se lanza una excepción BadCredentialsException
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "path", "/auth/login",
                            "message", "Credenciales erróneas",
                            "timestamp", new Date()
                    )
            );
        }
    }
}
