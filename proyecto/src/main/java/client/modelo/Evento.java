package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Evento {
    @JsonProperty
    private TipoEvento tipo;
    @JsonProperty
    private Posicion origen;
    @JsonProperty
    private Posicion destino;

    public Evento() {
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public Posicion getOrigen() {
        return origen;
    }

    public Posicion getDestino() {
        return destino;
    }
}
