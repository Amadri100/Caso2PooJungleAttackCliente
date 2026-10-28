package client.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import client.constantes.UIConstants;
import client.controlador.GameController;
import client.mensajes.TipoMensaje;
import client.modelo.Animal;
import client.modelo.Bandera;
import client.modelo.Cafe;
import client.modelo.DatosEnviar;
import client.modelo.Entidad;
import client.modelo.Evento;
import client.modelo.GameModel;
import client.modelo.Jugador;
import client.modelo.Nodo;
import client.modelo.Punto;
import client.modelo.Posicion;
import client.modelo.Zona;
import client.util.Observer;

public class GamePanel extends JPanel implements MouseListener, Observer {
    private static final Color COLOR_FONDO = new Color(238, 242, 226);
    private static final Color COLOR_NODO = new Color(90, 105, 78);
    private static final Color COLOR_ANIMAL_LOCAL = new Color(45, 99, 155);
    private static final Color COLOR_ANIMAL_OPONENTE = new Color(164, 71, 65);
    private static final Color COLOR_CAFE = new Color(134, 83, 37);
    private static final Color COLOR_BANDERA = new Color(190, 53, 53);

    private final GameModel model;
    private final GameController controlador;
    private final Timer timer;
    private final Rectangle2D.Double limites = new Rectangle2D.Double();
    private final Map<Evento, Long> eventosActivos = new HashMap<>();

    public GamePanel(GameModel model, GameController controlador) {
        this.model = Objects.requireNonNull(model, "El modelo es obligatorio.");
        this.controlador = Objects.requireNonNull(controlador, "El controlador es obligatorio.");
        setBackground(COLOR_FONDO);
        setPreferredSize(new Dimension(700, 500));
        addMouseListener(this);
        model.agregarObserver(this);
        timer = new Timer(Math.max(1, 1000 / UIConstants.FPS), event -> repaint());
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    @Override
    public void update(TipoMensaje tipo) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> update(tipo));
            return;
        }
        if (tipo == TipoMensaje.STATE_UPDATE || tipo == TipoMensaje.GAME_STARTED) {
            controlador.getSeleccion().sincronizar(model.getAnimalesLocales());
        }
        registrarEventosNuevos();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            calcularLimites();
            dibujarZona(g);
            dibujarEntidades(g);
            dibujarEventos(g);
            if (model.getDatos() == null) {
                g.setColor(new Color(75, 82, 67));
                String mensaje = "Esperando datos de la partida";
                FontMetrics metrics = g.getFontMetrics();
                g.drawString(mensaje, (getWidth() - metrics.stringWidth(mensaje)) / 2, getHeight() / 2);
            }
        } finally {
            g.dispose();
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        calcularLimites();
        Animal pulsado = animalEn(event.getX(), event.getY());
        if (pulsado != null) {
            if (esAnimalLocal(pulsado)) {
                if (controlador.getSeleccion().estaSeleccionado(pulsado)) {
                    controlador.getSeleccion().deseleccionar(pulsado);
                } else {
                    controlador.getSeleccion().seleccionar(pulsado);
                }
            } else if (!controlador.getSeleccion().getSeleccionados().isEmpty()) {
                controlador.atacar(pulsado);
            }
            repaint();
            return;
        }

        Nodo nodo = nodoEn(event.getX(), event.getY());
        if (nodo != null && !controlador.getSeleccion().getSeleccionados().isEmpty()) {
            controlador.moverSeleccion(nodo.getId());
            controlador.getSeleccion().deseleccionarTodo();
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    private void calcularLimites() {
        double minimoX = Double.POSITIVE_INFINITY;
        double minimoY = Double.POSITIVE_INFINITY;
        double maximoX = Double.NEGATIVE_INFINITY;
        double maximoY = Double.NEGATIVE_INFINITY;

        DatosEnviar datos = model.getDatos();
        if (datos != null) {
            Zona zona = datos.getZonaActual();
            if (zona != null) {
                for (Nodo nodo : zona.getNodos()) {
                    Punto punto = nodo.getPosicion();
                    if (punto != null) {
                        minimoX = Math.min(minimoX, punto.getX());
                        minimoY = Math.min(minimoY, punto.getY());
                        maximoX = Math.max(maximoX, punto.getX());
                        maximoY = Math.max(maximoY, punto.getY());
                    }
                }
            }
            for (Entidad entidad : datos.getEntidadesDisponibles()) {
                Punto punto = puntoDe(entidad);
                if (punto != null) {
                    minimoX = Math.min(minimoX, punto.getX());
                    minimoY = Math.min(minimoY, punto.getY());
                    maximoX = Math.max(maximoX, punto.getX());
                    maximoY = Math.max(maximoY, punto.getY());
                }
            }
        }

        if (!Double.isFinite(minimoX) || !Double.isFinite(minimoY)
                || !Double.isFinite(maximoX) || !Double.isFinite(maximoY)) {
            limites.setRect(0, 0, 1, 1);
            return;
        }
        double ancho = Math.max(1, maximoX - minimoX);
        double alto = Math.max(1, maximoY - minimoY);
        limites.setRect(minimoX - ancho * 0.05, minimoY - alto * 0.05, ancho * 1.1, alto * 1.1);
    }

    private void dibujarZona(Graphics2D g) {
        DatosEnviar datos = model.getDatos();
        if (datos == null || datos.getZonaActual() == null) {
            return;
        }
        g.setColor(COLOR_NODO);
        for (Nodo nodo : datos.getZonaActual().getNodos()) {
            if (nodo.getPosicion() == null) {
                continue;
            }
            Point2D punto = mundoAPantalla(nodo.getPosicion());
            int x = (int) punto.getX();
            int y = (int) punto.getY();
            g.fillOval(x - 5, y - 5, 10, 10);
            g.drawString(Integer.toString(nodo.getId()), x + 7, y - 7);
        }
    }

    private void dibujarEntidades(Graphics2D g) {
        DatosEnviar datos = model.getDatos();
        if (datos == null) {
            return;
        }
        for (Entidad entidad : datos.getEntidadesDisponibles()) {
            Punto posicion = puntoDe(entidad);
            if (posicion == null) {
                continue;
            }
            Point2D pantalla = mundoAPantalla(posicion);
            int tamano = entidad instanceof Animal ? UIConstants.ANIMAL_SIZE_PX : 16;
            g.setColor(colorDe(entidad));
            g.fillOval((int) pantalla.getX() - tamano / 2, (int) pantalla.getY() - tamano / 2, tamano, tamano);
            if (entidad instanceof Animal animal
                    && controlador.getSeleccion().estaSeleccionado(animal)) {
                g.setColor(Color.YELLOW.darker());
                g.setStroke(new BasicStroke(3));
                g.drawOval((int) pantalla.getX() - tamano / 2 - 3, (int) pantalla.getY() - tamano / 2 - 3,
                        tamano + 6, tamano + 6);
                g.setStroke(new BasicStroke(1));
            }
        }
    }

    private void dibujarEventos(Graphics2D g) {
        long ahora = System.currentTimeMillis();
        Iterator<Map.Entry<Evento, Long>> iterador = eventosActivos.entrySet().iterator();
        while (iterador.hasNext()) {
            Map.Entry<Evento, Long> entrada = iterador.next();
            long transcurrido = ahora - entrada.getValue();
            if (transcurrido >= UIConstants.DURACION_EVENTO_MS) {
                iterador.remove();
                continue;
            }
            Evento evento = entrada.getKey();
            Punto origen = puntoDe(evento.getOrigen());
            Punto destino = puntoDe(evento.getDestino());
            if (origen == null || destino == null) {
                continue;
            }
            float opacidad = 1f - (float) transcurrido / UIConstants.DURACION_EVENTO_MS;
            g.setColor(new Color(220, 55, 45, Math.max(0, Math.min(255, (int) (opacidad * 255)))));
            g.setStroke(new BasicStroke(3));
            Point2D a = mundoAPantalla(origen);
            Point2D b = mundoAPantalla(destino);
            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
        }
        g.setStroke(new BasicStroke(1));
    }

    private void registrarEventosNuevos() {
        DatosEnviar datos = model.getDatos();
        if (datos == null) {
            eventosActivos.clear();
            return;
        }
        long ahora = System.currentTimeMillis();
        for (Evento evento : datos.getEventos()) {
            eventosActivos.putIfAbsent(evento, ahora);
        }
    }

    private Point2D mundoAPantalla(Punto punto) {
        double margen = UIConstants.MARGEN_MAPA_PX;
        double escala = escalaPantalla();
        double dibujoAncho = limites.width * escala;
        double dibujoAlto = limites.height * escala;
        double desplazamientoX = (getWidth() - dibujoAncho) / 2;
        double desplazamientoY = (getHeight() - dibujoAlto) / 2;
        return new Point2D.Double(
                desplazamientoX + (punto.getX() - limites.x) * escala,
                desplazamientoY + dibujoAlto - (punto.getY() - limites.y) * escala);
    }

    private Punto pantallaAMundo(int x, int y) {
        double escala = escalaPantalla();
        double dibujoAncho = limites.width * escala;
        double dibujoAlto = limites.height * escala;
        double desplazamientoX = (getWidth() - dibujoAncho) / 2;
        double desplazamientoY = (getHeight() - dibujoAlto) / 2;
        return new Punto(
                limites.x + (x - desplazamientoX) / escala,
                limites.y + (dibujoAlto - (y - desplazamientoY)) / escala);
    }

    private double escalaPantalla() {
        double margen = UIConstants.MARGEN_MAPA_PX;
        double anchoUtil = Math.max(1, getWidth() - margen * 2.0);
        double altoUtil = Math.max(1, getHeight() - margen * 2.0);
        return Math.min(anchoUtil / limites.width, altoUtil / limites.height);
    }

    private Animal animalEn(int x, int y) {
        DatosEnviar datos = model.getDatos();
        if (datos == null) {
            return null;
        }
        Animal cercano = null;
        double distanciaMinima = UIConstants.HIT_RADIUS_PX;
        for (Animal animal : animalesVisibles(datos)) {
            Punto posicion = puntoDe(animal);
            if (posicion == null) {
                continue;
            }
            double distancia = mundoAPantalla(posicion).distance(x, y);
            if (distancia <= distanciaMinima) {
                cercano = animal;
                distanciaMinima = distancia;
            }
        }
        return cercano;
    }

    private Nodo nodoEn(int x, int y) {
        Zona zona = model.getDatos() == null ? null : model.getDatos().getZonaActual();
        if (zona == null) {
            return null;
        }
        Punto mundo = pantallaAMundo(x, y);
        Nodo cercano = null;
        double distanciaMinima = UIConstants.HIT_RADIUS_PX / escalaPantalla();
        for (Nodo nodo : zona.getNodos()) {
            if (nodo.getPosicion() == null) {
                continue;
            }
            Punto posicion = nodo.getPosicion();
            double distancia = Math.hypot(posicion.getX() - mundo.getX(), posicion.getY() - mundo.getY());
            if (distancia <= distanciaMinima) {
                cercano = nodo;
                distanciaMinima = distancia;
            }
        }
        return cercano;
    }

    private boolean esAnimalLocal(Animal animal) {
        for (Animal local : model.getAnimalesLocales()) {
            if (mismoAnimal(local, animal)) {
                return true;
            }
        }
        return false;
    }

    private List<Animal> animalesVisibles(DatosEnviar datos) {
        Map<String, Animal> animales = new HashMap<>();
        for (Jugador jugador : datos.getJugadores()) {
            if (jugador == null) {
                continue;
            }
            for (Animal animal : jugador.getAnimales()) {
                animales.put(clave(animal), animal);
            }
        }
        for (Animal animal : datos.getAnimalesVisibles()) {
            animales.put(clave(animal), animal);
        }
        return new ArrayList<>(animales.values());
    }

    private String clave(Animal animal) {
        return animal.getIdPack() + ":" + animal.getId();
    }

    private boolean mismoAnimal(Animal primero, Animal segundo) {
        return primero.getIdPack() == segundo.getIdPack() && primero.getId() == segundo.getId();
    }

    private Punto puntoDe(Entidad entidad) {
        return entidad == null ? null : puntoDe(entidad.getPosicion());
    }

    private Punto puntoDe(Posicion posicion) {
        return posicion == null ? null : posicion.getPunto();
    }

    private Color colorDe(Entidad entidad) {
        if (entidad instanceof Animal animal) {
            return esAnimalLocal(animal) ? COLOR_ANIMAL_LOCAL : COLOR_ANIMAL_OPONENTE;
        }
        if (entidad instanceof Cafe cafe) {
            return cafe.estaDisponible() ? COLOR_CAFE : Color.GRAY;
        }
        if (entidad instanceof Bandera) {
            return COLOR_BANDERA;
        }
        return Color.DARK_GRAY;
    }
}
