package client.modelo;

import client.mensajes.MensajeAnimalSelection;
import client.mensajes.TipoMensaje;

public class AccionAnimalSelection extends AccionMensaje {

    public AccionAnimalSelection(MensajeAnimalSelection mensaje) {
        super(mensaje);
    }

    @Override
    public void ejecutar(GameModel model) {
        MensajeAnimalSelection msg = (MensajeAnimalSelection) getMensaje();
        model.setEspeciesDisponibles(msg.getEspeciesDisponibles());
        model.setSeleccionOponente(msg.getSeleccionOponente());
        model.notificar(TipoMensaje.ANIMAL_SELECTION);
    }
}
