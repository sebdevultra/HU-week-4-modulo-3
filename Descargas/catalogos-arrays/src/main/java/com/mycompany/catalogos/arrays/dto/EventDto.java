package com.mycompany.catalogos.arrays.dto;

public class EventDto {

    private Long id;
    private String nombre;
    private String date;
    private VenueDto venues;
    private Double precio;

    public EventDto(Long id, String nombre, String date, VenueDto venues, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.date = date;
        this.venues = venues;
        this.precio = precio;
    }

    public EventDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public VenueDto getVenues() {
        return venues;
    }

    public void setVenues(VenueDto venues) {
        this.venues = venues;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

}
