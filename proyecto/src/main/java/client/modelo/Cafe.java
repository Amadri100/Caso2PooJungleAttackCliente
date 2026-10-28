package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cafe extends Entidad {
    @JsonProperty
    private boolean consumido;

    public Cafe() {
        super(TipoEntidad.CAFE);
    }

    public boolean isConsumido() {
        return consumido;
    }

    public boolean estaDisponible() {
        return !consumido;
    }
}
