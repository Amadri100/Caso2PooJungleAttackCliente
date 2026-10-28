package client.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Animal extends Entidad {
    @JsonProperty
    private int idPack;
    @JsonProperty
    private TipoEspecie especie;
    @JsonProperty
    private int energia;

    public Animal() {
        super(TipoEntidad.ANIMAL);
    }

    public int getIdPack() {
        return idPack;
    }

    public TipoEspecie getEspecie() {
        return especie;
    }

    public int getEnergia() {
        return energia;
    }

    public boolean estaVivo() {
        return energia > 0;
    }

    public double getX() {
        return getPosicion().getPunto().getX();
    }

    public double getY() {
        return getPosicion().getPunto().getY();
    }

    public int[] getIdCompuesto() {
        return new int[]{idPack, getId()};
    }
}
