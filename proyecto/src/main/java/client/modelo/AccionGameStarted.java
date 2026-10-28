package client.modelo;
 
import client.mensajes.MensajeGameStarted;
import client.mensajes.TipoMensaje;
 
public class AccionGameStarted extends AccionMensaje {
 
    public AccionGameStarted(MensajeGameStarted mensaje) {
        super(mensaje);
    }
 
    @Override
    public void ejecutar(GameModel model) {
        MensajeGameStarted msg = (MensajeGameStarted) getMensaje();
        model.actualizarEstado(msg.getDatos());
        model.notificar(TipoMensaje.GAME_STARTED);
    }
}
 