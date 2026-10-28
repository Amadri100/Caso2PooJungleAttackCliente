package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bandera extends Entidad {
    @JsonProperty
    private int idJugador;
    @JsonProperty
    private String color;

    public Bandera() {
        super(TipoEntidad.BANDERA);
    }

    public int getIdJugador() {
        return idJugador;
    }

    public String getColor() {
        return color;
    }
}
