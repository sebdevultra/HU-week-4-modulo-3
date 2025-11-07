package com.mycompany.catalogos.arrays.service;

import com.mycompany.catalogos.arrays.dto.EventDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final List<EventDto> events = new ArrayList<>();
    public final ServiceVenues serviceVenues;

    public EventService(ServiceVenues serviceVenues) {
        this.serviceVenues = serviceVenues;
    }

    private long idCounter = 1;

    public EventDto save(EventDto event) {
        if (event.getVenues() == null || event.getVenues().getId() == null) {

            throw new RuntimeException("debe llenar el campo");

        }

        var venueId = serviceVenues.BuscarPorId(event.getVenues().getId());
        event.setVenues(venueId);
        event.setId(idCounter++);
        events.add(event);
        return event;

    }

    public EventDto BuscarPorId(Long id) {

        for (EventDto event : events) {

            if (event.getId().equals(id)) {
                return event;

            }

        }

        throw new RuntimeException("Nose encontro el evento");

    }

    public List<EventDto> listar() {

        return events;

    }

    public void eliminar(Long id) {

        var event = BuscarPorId(id);

        if (event.getId().equals(id)) {
            events.remove(event);

        }

    }

}
