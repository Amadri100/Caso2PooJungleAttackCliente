package client.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Zona {
    @JsonProperty
    private int id;
    @JsonProperty
    private List<Nodo> nodos = new ArrayList<>();

    public Zona() {
    }

    public int getId() {
        return id;
    }

    public List<Nodo> getNodos() {
        return List.copyOf(nodos);
    }

    public Nodo getNodo(int idNodo) {
        return nodos.stream()
                .filter(nodo -> nodo.getId() == idNodo)
                .findFirst()
                .orElse(null);
    }
}
