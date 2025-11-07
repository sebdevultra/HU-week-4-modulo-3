package com.mycompany.catalogos.arrays.controlador;

import com.mycompany.catalogos.arrays.dto.VenueDto;
import com.mycompany.catalogos.arrays.service.ServiceVenues;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/venues")
public class VenuesControlador {

    private final ServiceVenues service;

    public VenuesControlador(ServiceVenues service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(service.listar());

    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody VenueDto venue) {
        return ResponseEntity.ok(service.save(venue));

    }

    @GetMapping("/{id}")
   public ResponseEntity<VenueDto> buscarPorId(@PathVariable("id") Long id) {
        VenueDto resultado = service.BuscarPorId(id);
        return ResponseEntity.ok(resultado);
    }
   @DeleteMapping("/{id}")
   public ResponseEntity<String>eliminar(@PathVariable("id") Long id ){
       service.eliminar(id);
       return ResponseEntity.ok("eliminado");
   
   }

}
