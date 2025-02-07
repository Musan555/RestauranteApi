package org.example.restauranteapi.controller;

import jakarta.validation.Valid;
import org.example.restauranteapi.entity.Cliente;
import org.example.restauranteapi.entity.Mesa;
import org.example.restauranteapi.entity.Reserva;
import org.example.restauranteapi.repository.ClienteRepository;
import org.example.restauranteapi.repository.MesaRepository;
import org.example.restauranteapi.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getListClientes(){
        var clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }
    @PostMapping("/clientes")
    public ResponseEntity<Cliente> insertClientes (@RequestBody @Valid Cliente cliente){
        var clienteGuardado = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> getReserva(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> ResponseEntity.ok().body(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> editCliente(@PathVariable Long id, @RequestBody @Valid Cliente nuevoCliente){
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(nuevoCliente.getNombre());
                    cliente.setEmail(nuevoCliente.getEmail());
                    cliente.setTelefono(nuevoCliente.getTelefono());
                    return ResponseEntity.ok(clienteRepository.save(cliente));
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }
    
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.delete(cliente);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
