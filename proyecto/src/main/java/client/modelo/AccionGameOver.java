package client.modelo;

import client.mensajes.MensajeGameOver;
import client.mensajes.TipoMensaje;

public class AccionGameOver extends AccionMensaje {

    public AccionGameOver(MensajeGameOver mensaje) {
        super(mensaje);
    }

    @Override
    public void ejecutar(GameModel model) {
        MensajeGameOver msg = (MensajeGameOver) getMensaje();
        model.setGanador(msg.getGanador());
        model.notificar(TipoMensaje.GAME_OVER);
    }
}
