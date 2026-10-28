package client.modelo;
 
import client.mensajes.MensajeMatchJoined;
import client.mensajes.TipoMensaje;
 
public class AccionMatchJoined extends AccionMensaje {
 
    public AccionMatchJoined(MensajeMatchJoined mensaje) {
        super(mensaje);
    }
 
    @Override
    public void ejecutar(GameModel model) {
        MensajeMatchJoined msg = (MensajeMatchJoined) getMensaje();
        model.setCodigoPartida(msg.getCodigo());
        model.setJugadoresEnPartida(msg.getJugadores());
        model.setUltimoError(null);
        model.notificar(TipoMensaje.MATCH_JOINED);
    }
}
 