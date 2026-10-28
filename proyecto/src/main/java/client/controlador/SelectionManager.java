package client.controlador;

import java.util.ArrayList;
import java.util.List;

import client.modelo.Animal;

/**
 * Controla la seleccion de animales por clic del usuario.
 * getIdsSeleccionados() devuelve los int[]{idPack, idAnimal} listos para MensajeMoveAnimals.
 * Solo se pueden seleccionar animales vivos.
 */
public class SelectionManager {

    private final List<Animal> animalesSeleccionados;

    public SelectionManager() {
        this.animalesSeleccionados = new ArrayList<>();
    }

    public void seleccionar(Animal animal) {
        if (animal == null || !animal.estaVivo() || estaSeleccionado(animal)) {
            return;
        }
        animalesSeleccionados.add(animal);
    }

    public void deseleccionar(Animal animal) {
        if (animal == null) {
            return;
        }
        animalesSeleccionados.removeIf(a -> mismoAnimal(a, animal));
    }

    public void deseleccionarTodo() {
        animalesSeleccionados.clear();
    }

    public boolean estaSeleccionado(Animal animal) {
        if (animal == null) {
            return false;
        }
        for (Animal a : animalesSeleccionados) {
            if (mismoAnimal(a, animal)) {
                return true;
            }
        }
        return false;
    }

    public List<Animal> getSeleccionados() {
        return List.copyOf(animalesSeleccionados);
    }

    public List<int[]> getIdsSeleccionados() {
        List<int[]> ids = new ArrayList<>();
        for (Animal a : animalesSeleccionados) {
            ids.add(a.getIdCompuesto());
        }
        return ids;
    }

    /**
     * Reemplaza los animales seleccionados por su version actual en vigentes
     * y descarta los que ya no existen o murieron.
     */
    public void sincronizar(List<Animal> vigentes) {
        if (vigentes == null) {
            animalesSeleccionados.clear();
            return;
        }
        List<Animal> actualizados = new ArrayList<>();
        for (Animal seleccionado : animalesSeleccionados) {
            for (Animal vigente : vigentes) {
                if (mismoAnimal(seleccionado, vigente)) {
                    if (vigente.estaVivo()) {
                        actualizados.add(vigente);
                    }
                    break;
                }
            }
        }
        animalesSeleccionados.clear();
        animalesSeleccionados.addAll(actualizados);
    }

    private static boolean mismoAnimal(Animal a, Animal b) {
        return a.getIdPack() == b.getIdPack() && a.getId() == b.getId();
    }
}