package client.controlador;

import java.io.IOException;

import client.mensajes.MensajeAttack;
import client.mensajes.MensajeCreateMatch;
import client.mensajes.MensajeIndicarNombre;
import client.mensajes.MensajeJoinMatch;
import client.mensajes.MensajeJugador;
import client.mensajes.MensajeMoveAnimals;
import client.mensajes.MensajeSelectAnimal;
import client.mensajes.MensajeViewZone;
import client.mensajes.TipoMensaje;
import client.modelo.Animal;
import client.modelo.GameModel;
import client.modelo.TipoEspecie;
import client.red.MessageDispatcher;
import client.red.ServerConnection;

public class GameController {

    private final GameModel model;
    private final MessageDispatcher dispatcher;
    private final SelectionManager seleccion;
    private ServerConnection conexion;
    private Thread hiloConexion;

    public GameController(GameModel model, MessageDispatcher dispatcher) {
        this.model = model;
        this.dispatcher = dispatcher;
        this.seleccion = new SelectionManager();
    }

    public boolean conectar(String host, int puerto, String nickname) {
        if (conexion != null) {
            return false;
        }
        if (!nombreValido(nickname)) {
            reportarError("El nombre no es valido.");
            return false;
        }
        String nombre = nickname.trim();
        try {
            conexion = new ServerConnection(host, puerto, dispatcher);
        } catch (IOException e) {
            conexion = null;
            reportarError("No se pudo conectar con el servidor.");
            return false;
        }
        model.setNickname(nombre);
        hiloConexion = new Thread(conexion, "conexion-servidor");
        hiloConexion.setDaemon(true);
        hiloConexion.start();
        enviar(new MensajeIndicarNombre(nombre));
        return true;
    }

    public void desconectar() {
        if (conexion != null) {
            conexion.desconectar();
        }
        if (hiloConexion != null) {
            hiloConexion.interrupt();
        }
        conexion = null;
        hiloConexion = null;
        seleccion.deseleccionarTodo();
    }

    public void crearPartida() {
        enviar(new MensajeCreateMatch(model.getNickname()));
    }

    public void unirsePartida(String codigo) {
        if (!codigoValido(codigo)) {
            reportarError("El codigo de partida no es valido.");
            return;
        }
        enviar(new MensajeJoinMatch(model.getNickname(), codigo.trim()));
    }

    public void seleccionarEspecie(TipoEspecie especie) {
        if (especie == null || especie == TipoEspecie.NINGUNA) {
            return;
        }
        enviar(new MensajeSelectAnimal(model.getNickname(), especie));
    }

    public void moverSeleccion(int idNodoDestino) {
        if (seleccion.getSeleccionados().isEmpty()) {
            return;
        }
        int[] destino = new int[]{model.getZonaActualVisible(), idNodoDestino};
        enviar(new MensajeMoveAnimals(model.getNickname(), seleccion.getIdsSeleccionados(), destino));
    }

    public void atacar(Animal enemigo) {
        if (enemigo == null) {
            return;
        }
        for (Animal atacante : seleccion.getSeleccionados()) {
            enviar(new MensajeAttack(model.getNickname(), atacante.getIdCompuesto(), enemigo.getIdCompuesto()));
        }
    }

    public void verZona(int idZona) {
        enviar(new MensajeViewZone(model.getNickname(), idZona));
        model.setZonaActualVisible(idZona);
    }

    public SelectionManager getSeleccion() {
        return seleccion;
    }

    public GameModel getModel() {
        return model;
    }

    private void enviar(MensajeJugador msg) {
        if (conexion == null) {
            reportarError("No hay conexion con el servidor.");
            return;
        }
        msg.setNombre(model.getNickname());
        conexion.enviar(msg);
    }

    private boolean nombreValido(String nickname) {
        return nickname != null && !nickname.isBlank();
    }

    private boolean codigoValido(String codigo) {
        return codigo != null && !codigo.isBlank();
    }

    private void reportarError(String error) {
        model.setUltimoError(error);
        model.notificar(TipoMensaje.CASO);
    }
}