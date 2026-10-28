package client.modelo;

import client.mensajes.MensajeStateUpdate;

public class AccionStateUpdate extends AccionMensaje {

    public AccionStateUpdate(MensajeStateUpdate mensaje) {
        super(mensaje);
    }

    @Override
    public void ejecutar(GameModel model) {
        MensajeStateUpdate msg = (MensajeStateUpdate) getMensaje();
        // actualizarEstado ya notifica STATE_UPDATE
        model.actualizarEstado(msg.getDatos());
    }
}
