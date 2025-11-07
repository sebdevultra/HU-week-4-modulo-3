package com.mycompany.catalogos.arrays.controlador;

import com.mycompany.catalogos.arrays.dto.EventDto;
import com.mycompany.catalogos.arrays.service.EventService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/eventos")
public class EventControlador {
    
    
    private final EventService service;

    public EventControlador(EventService service) {
        this.service = service;
    }

    
    
    @PostMapping
    public  ResponseEntity<EventDto>crear(@RequestBody EventDto event){
        return ResponseEntity.ok(service.save(event));
        
        
    
    
    }
    
    
    @GetMapping
    public ResponseEntity<?>listar(){
        
        return ResponseEntity.ok(service.listar());
    
    
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?>BuscarPorId(@PathVariable Long id){
        
        return ResponseEntity.ok(service.BuscarPorId(id));
    
    
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String>eliminar(@PathVariable Long id){
        
        service.eliminar(id);
        
        return ResponseEntity.ok("eliminado");
    
    }
    
    
    

}
