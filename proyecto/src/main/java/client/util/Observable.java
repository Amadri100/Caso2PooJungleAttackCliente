package client.util;
 
import java.util.ArrayList;
import java.util.List;

import client.mensajes.TipoMensaje;
 
public abstract class Observable {
 
    private final List<Observer> observers = new ArrayList<>();
 
    public void agregarObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }
 
    public void notificar(TipoMensaje tipo) {
        for (Observer o : new ArrayList<>(observers)) {
            o.update(tipo);
        }
    }
 
}