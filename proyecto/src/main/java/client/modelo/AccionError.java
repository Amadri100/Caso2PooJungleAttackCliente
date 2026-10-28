package client.modelo;
 
import client.mensajes.MensajeCaso;
import client.mensajes.TipoMensaje;
 
public class AccionError extends AccionMensaje {
 
    public AccionError(MensajeCaso mensaje) {
        super(mensaje);
    }
 
    @Override
    public void ejecutar(GameModel model) {
        MensajeCaso msg = (MensajeCaso) getMensaje();
        // caso == true: operacion exitosa, no hay error que guardar
        model.setUltimoError(msg.isCaso() ? null : msg.getDescripcion());
        model.notificar(TipoMensaje.CASO);
    }
}