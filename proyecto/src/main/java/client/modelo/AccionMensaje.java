package client.modelo;

import client.mensajes.Mensaje;
 

public abstract class AccionMensaje {
    private Mensaje mensaje;
    public abstract void ejecutar(GameModel model);
}
