package client.modelo;

import client.mensajes.MensajeMatchCreated;
import client.mensajes.TipoMensaje;

public class AccionMatchCreated extends AccionMensaje {

    public AccionMatchCreated(MensajeMatchCreated mensaje) {
        super(mensaje);
    }

    @Override
    public void ejecutar(GameModel model) {
        MensajeMatchCreated msg = (MensajeMatchCreated) getMensaje();
        model.setCodigoPartida(msg.getCodigo());
        model.setUltimoError(null);
        model.notificar(TipoMensaje.MATCH_CREATED);
    }
}
