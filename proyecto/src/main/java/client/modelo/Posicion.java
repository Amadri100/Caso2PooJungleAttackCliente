package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Posicion {
    @JsonProperty
    private Nodo nodo;
    @JsonProperty
    private Punto punto;

    public Posicion() {
    }

    public Nodo getNodo() {
        return nodo;
    }

    public Punto getPunto() {
        return punto;
    }
}
