package client.modelo;

import client.mensajes.Mensaje;

public abstract class AccionMensaje {

    private final Mensaje mensaje;

    protected AccionMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    protected Mensaje getMensaje() {
        return mensaje;
    }

    public abstract void ejecutar(GameModel model);
}
