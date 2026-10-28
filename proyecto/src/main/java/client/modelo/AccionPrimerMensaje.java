package client.modelo;

import client.mensajes.MensajePrimerMensaje;
import client.mensajes.TipoMensaje;

public class AccionPrimerMensaje extends AccionMensaje {

    public AccionPrimerMensaje(MensajePrimerMensaje mensaje) {
        super(mensaje);
    }

    @Override
    public void ejecutar(GameModel model) {
        MensajePrimerMensaje msg = (MensajePrimerMensaje) getMensaje();
        if (msg.isExito()) {
            model.setNombreConfirmado(true);
            model.setUltimoError(null);
        } else {
            model.setNombreConfirmado(false);
            model.setUltimoError(msg.getRazon());
        }
        model.notificar(TipoMensaje.PRIMER_MENSAJE);
    }
}
