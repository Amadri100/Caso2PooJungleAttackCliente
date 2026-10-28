package client.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.modelo.AccionMensaje;
import client.mensajes.TipoMensaje;
import client.util.Observable;

public class GameModel extends Observable {

    private String nickname;
    private String codigoPartida;
    private boolean nombreConfirmado;
    private List<String> jugadoresEnPartida;
    private List<TipoEspecie> especiesDisponibles;
    private TipoEspecie seleccionOponente;
    private int zonaActualVisible;
    private DatosEnviar datos;
    private List<Animal> animalesLocales;
    private final Map<String, Animal> animalesConocidos;
    private String ganador;
    private String ultimoError;

    public GameModel() {
        this.jugadoresEnPartida = new ArrayList<>();
        this.especiesDisponibles = new ArrayList<>();
        this.animalesLocales = new ArrayList<>();
        this.animalesConocidos = new HashMap<>();
        this.seleccionOponente = TipoEspecie.NINGUNA;
        this.zonaActualVisible = 0;
    }

    // ---------- Nickname ----------

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isNombreConfirmado() {
        return nombreConfirmado;
    }

    public void setNombreConfirmado(boolean nombreConfirmado) {
        this.nombreConfirmado = nombreConfirmado;
    }

    // ---------- Datos de la partida (los modifican las acciones) ----------

    public String getCodigoPartida() {
        return codigoPartida;
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    public List<String> getJugadoresEnPartida() {
        return List.copyOf(jugadoresEnPartida);
    }

    public void setJugadoresEnPartida(List<String> jugadores) {
        this.jugadoresEnPartida = jugadores == null ? new ArrayList<>() : new ArrayList<>(jugadores);
    }

    public List<TipoEspecie> getEspeciesDisponibles() {
        return List.copyOf(especiesDisponibles);
    }

    public void setEspeciesDisponibles(List<TipoEspecie> especies) {
        this.especiesDisponibles = especies == null ? new ArrayList<>() : new ArrayList<>(especies);
    }

    public TipoEspecie getSeleccionOponente() {
        return seleccionOponente;
    }

    public void setSeleccionOponente(TipoEspecie seleccionOponente) {
        this.seleccionOponente = seleccionOponente;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public void setUltimoError(String ultimoError) {
        this.ultimoError = ultimoError;
    }

    // ---------- Estado de juego ----------

    public DatosEnviar getDatos() {
        return datos;
    }

    public Jugador getJugadorLocal() {
        if (datos == null || nickname == null) {
            return null;
        }
        for (Jugador j : datos.getJugadores()) {
            if (nickname.equals(j.getNickname())) {
                return j;
            }
        }
        return null;
    }

    public Jugador getOponente() {
        if (datos == null || nickname == null) {
            return null;
        }
        for (Jugador j : datos.getJugadores()) {
            if (!nickname.equals(j.getNickname())) {
                return j;
            }
        }
        return null;
    }

    public List<Animal> getAnimalesLocales() {
        return List.copyOf(animalesLocales);
    }

    public int getEnergiaConocida(int idPack) {
        int total = 0;
        for (Animal a : animalesConocidos.values()) {
            if (a.getIdPack() == idPack) {
                total += a.getEnergia();
            }
        }
        return total;
    }

    public int getVivosConocidos(int idPack) {
        int total = 0;
        for (Animal a : animalesConocidos.values()) {
            if (a.getIdPack() == idPack && a.estaVivo()) {
                total++;
            }
        }
        return total;
    }

    public int getZonaActualVisible() {
        return zonaActualVisible;
    }

    public void setZonaActualVisible(int zonaId) {
        this.zonaActualVisible = zonaId;
        notificar(TipoMensaje.VIEW_ZONE);
    }

    public void actualizarEstado(DatosEnviar datos) {
        this.datos = datos;
        Jugador local = getJugadorLocal();
        this.animalesLocales = local == null ? new ArrayList<>() : new ArrayList<>(local.getAnimales());
        recordarAnimales(datos);
        notificar(TipoMensaje.STATE_UPDATE);
    }

    // ---------- Acciones y ciclo de vida ----------

    public void realizarAccion(AccionMensaje accion) {
        accion.ejecutar(this);
    }

    public void onDesconexion() {
        this.ultimoError = "Conexion perdida con el servidor";
        notificar(TipoMensaje.CASO);
    }

    /**
     * Limpia el estado de la partida. Conserva el nickname, su confirmacion
     * y la conexion.
     */
    public void reiniciarPartida() {
        this.codigoPartida = null;
        this.jugadoresEnPartida = new ArrayList<>();
        this.especiesDisponibles = new ArrayList<>();
        this.seleccionOponente = TipoEspecie.NINGUNA;
        this.zonaActualVisible = 0;
        this.datos = null;
        this.animalesLocales = new ArrayList<>();
        this.animalesConocidos.clear();
        this.ganador = null;
        this.ultimoError = null;
    }

    private void recordarAnimales(DatosEnviar datos) {
        if (datos == null) {
            return;
        }
        for (Jugador j : datos.getJugadores()) {
            for (Animal a : j.getAnimales()) {
                animalesConocidos.put(a.getIdPack() + "-" + a.getId(), a);
            }
        }
    }
}