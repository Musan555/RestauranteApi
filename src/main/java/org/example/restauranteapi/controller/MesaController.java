package org.example.restauranteapi.controller;

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
public class MesaController {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/mesas")
    public ResponseEntity<List<Mesa>> getListMesas(){
        var mesas = mesaRepository.findAll();
        return ResponseEntity.ok(mesas);
    }
    @PostMapping("/mesas")
    public ResponseEntity<Mesa> insertMesas (@RequestBody Mesa mesa){
        var mesaGuardada = mesaRepository.save(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaGuardada);
    }

    @GetMapping("/mesas/{id}")
    public ResponseEntity<Mesa> getMesa(@PathVariable Long id) {
        return mesaRepository.findById(id)
                .map(mesa -> ResponseEntity.ok().body(mesa))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/mesas/{id}")
    public ResponseEntity<Mesa> editMesa(@PathVariable Long id, @RequestBody Mesa nuevaMesa){
        return mesaRepository.findById(id)
                .map(mesa -> {
                    mesa.setNumeroMesa(nuevaMesa.getNumeroMesa());
                    mesa.setDescripcion(nuevaMesa.getDescripcion());
                    return ResponseEntity.ok(mesaRepository.save(mesa));
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }
    
    @DeleteMapping("/mesas/{id}")
    public ResponseEntity<?> deleteMesa(@PathVariable Long id) {
        return mesaRepository.findById(id)
                .map(mesa -> {
                    mesaRepository.delete(mesa);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
