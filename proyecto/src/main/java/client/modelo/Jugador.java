package client.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Jugador {
    @JsonProperty
    private int id;
    @JsonProperty
    private String nickname;
    @JsonProperty
    private TipoEspecie especie;
    @JsonProperty
    private List<Animal> animales = new ArrayList<>();
    @JsonProperty
    private int cafesConsumidos;
    @JsonProperty
    private int botsEliminados;
    @JsonProperty
    private boolean conectado;

    private Jugador() {
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public TipoEspecie getEspecie() {
        return especie;
    }

    public List<Animal> getAnimales() {
        return List.copyOf(animales);
    }

    public int getCafesConsumidos() {
        return cafesConsumidos;
    }

    public int getBotsEliminados() {
        return botsEliminados;
    }

    public boolean isConectado() {
        return conectado;
    }

    public int getVivos() {
        return (int) animales.stream().filter(Animal::estaVivo).count();
    }

    public int getEnergiaTotal() {
        return animales.stream().mapToInt(Animal::getEnergia).sum();
    }
}
