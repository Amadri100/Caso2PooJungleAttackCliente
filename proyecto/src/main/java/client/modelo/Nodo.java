package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Nodo {
    @JsonProperty
    private int id;
    @JsonProperty
    private Punto posicion;
    @JsonProperty
    private int idZona;

    public Nodo() {
    }

    public int getId() {
        return id;
    }

    public Punto getPosicion() {
        return posicion;
    }

    public int getIdZona() {
        return idZona;
    }
}
