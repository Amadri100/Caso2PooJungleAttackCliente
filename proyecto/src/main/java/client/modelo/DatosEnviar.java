package client.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DatosEnviar {
    @JsonProperty
    private Jugador jugadorA;
    @JsonProperty
    private Jugador jugadorB;
    @JsonProperty
    private List<Entidad> entidadesDisponibles = new ArrayList<>();
    @JsonProperty
    private Zona zonaActual;
    @JsonProperty
    private List<Evento> eventos = new ArrayList<>();

    private DatosEnviar() {
    }

    public Jugador getJugadorA() {
        return jugadorA;
    }

    public Jugador getJugadorB() {
        return jugadorB;
    }

    public List<Entidad> getEntidadesDisponibles() {
        return List.copyOf(entidadesDisponibles);
    }

    public Jugador[] getJugadores() {
        Jugador[] jugadores = new Jugador[2];
        jugadores[0] = this.jugadorA;
        jugadores[1] = this.jugadorB;
        return jugadores;
    }

    public Zona getZonaActual() {
        return zonaActual;
    }

    public List<Evento> getEventos() {
        return List.copyOf(eventos);
    }

    public List<Animal> getAnimalesVisibles() {
        return entidadesDisponibles.stream()
                .filter(Animal.class::isInstance)
                .map(Animal.class::cast)
                .toList();
    }
}
