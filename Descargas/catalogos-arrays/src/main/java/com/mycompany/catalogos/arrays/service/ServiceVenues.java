package com.mycompany.catalogos.arrays.service;

import com.mycompany.catalogos.arrays.dto.VenueDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServiceVenues {

    private final List<VenueDto> venues = new ArrayList<>();

    private long idCounter = 1;

    public VenueDto save(VenueDto venue) {

        venue.setId(idCounter++);

        venues.add(venue);
        return venue;

    }

    public List<VenueDto> listar() {
        return venues;

    }

    public VenueDto BuscarPorId(Long id) {
        for (VenueDto venue : venues) {

            if (venue.getId().equals(id)) {

                return venue;

            }

        }

        throw new RuntimeException("No se encontro  el venues con la id   :" + id);

    }

    public void eliminar(Long id) {

        var venue = BuscarPorId(id);

        venues.remove(venue);

    }

}
