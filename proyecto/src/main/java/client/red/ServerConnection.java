package client.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import client.mensajes.Mensaje;
import client.util.Json;

public class ServerConnection implements Runnable {
    private static final System.Logger LOGGER = System.getLogger(ServerConnection.class.getName());

    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    private final MessageDispatcher dispatcher;
    private final AtomicBoolean desconexionNotificada = new AtomicBoolean();
    private volatile boolean activo = true;

    public ServerConnection(String host, int puerto, MessageDispatcher dispatcher) throws IOException {
        Objects.requireNonNull(host, "El host es obligatorio.");
        this.dispatcher = Objects.requireNonNull(dispatcher, "El dispatcher es obligatorio.");

        Socket nuevaConexion = new Socket(host, puerto);
        try {
            this.entrada = new BufferedReader(
                    new InputStreamReader(nuevaConexion.getInputStream(), StandardCharsets.UTF_8));
            this.salida = new PrintWriter(nuevaConexion.getOutputStream(), true, StandardCharsets.UTF_8);
            this.socket = nuevaConexion;
        } catch (IOException e) {
            nuevaConexion.close();
            throw e;
        }
    }

    @Override
    public void run() {
        try {
            String linea;
            while (activo && (linea = entrada.readLine()) != null) {
                dispatcher.despachar(linea);
            }
        } catch (IOException e) {
            if (activo) {
                LOGGER.log(System.Logger.Level.WARNING, "Se perdió la conexión con el servidor.", e);
            }
        } finally {
            activo = false;
            cerrarSocket();
            notificarDesconexion();
        }
    }

    public synchronized void enviar(Mensaje mensaje) {
        enviar(Json.mensajeAJson(mensaje));
    }

    public synchronized void enviar(String json) {
        Objects.requireNonNull(json, "El mensaje no puede ser null.");
        if (!isActivo()) {
            throw new IllegalStateException("La conexión con el servidor está cerrada.");
        }

        salida.println(json);
        if (salida.checkError()) {
            activo = false;
            cerrarSocket();
            notificarDesconexion();
            throw new IllegalStateException("No se pudo enviar el mensaje al servidor.");
        }
    }

    public void recibirAscender(Mensaje mensaje) {
        enviar(mensaje);
    }

    public boolean isActivo() {
        return activo && socket.isConnected() && !socket.isClosed();
    }

    public void desconectar() {
        activo = false;
        cerrarSocket();
        notificarDesconexion();
    }

    private void cerrarSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.WARNING, "No se pudo cerrar el socket.", e);
        }
    }

    private void notificarDesconexion() {
        if (desconexionNotificada.compareAndSet(false, true)) {
            dispatcher.conexionPerdida();
        }
    }
}
