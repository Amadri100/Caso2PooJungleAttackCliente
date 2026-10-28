package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipo",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Animal.class, name = "ANIMAL"),
        @JsonSubTypes.Type(value = Cafe.class, name = "CAFE"),
        @JsonSubTypes.Type(value = Bandera.class, name = "BANDERA")
})
public abstract class Entidad {
    @JsonProperty
    private int id;
    @JsonProperty
    private TipoEntidad tipo;
    @JsonProperty
    private Posicion posicion;

    protected Entidad() {
    }

    protected Entidad(TipoEntidad tipo) {
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public TipoEntidad getTipo() {
        return tipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }
}
