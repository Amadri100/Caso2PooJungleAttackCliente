package client.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.constantes.Constants;
import client.controlador.GameController;
import client.mensajes.TipoMensaje;
import client.modelo.GameModel;
import client.modelo.Jugador;
import client.util.Observer;

public class HUDPanel extends JPanel implements Observer {
    private final GameModel model;
    private final GameController controlador;
    private final JProgressBar barraVidaLocal = new JProgressBar(0, energiaMaxima());
    private final JProgressBar barraVidaOponente = new JProgressBar(0, energiaMaxima());
    private final JLabel etiquetaCafes = new JLabel();
    private final JLabel etiquetaCodigo = new JLabel();
    private final JLabel etiquetaZona = new JLabel();
    private final List<JButton> botonesZona = new ArrayList<>();

    public HUDPanel(GameModel model, GameController controlador) {
        this.model = Objects.requireNonNull(model, "El modelo es obligatorio.");
        this.controlador = Objects.requireNonNull(controlador, "El controlador es obligatorio.");
        construirUI();
        refrescar();
    }

    @Override
    public void update(TipoMensaje tipo) {
        if (SwingUtilities.isEventDispatchThread()) {
            refrescar();
        } else {
            SwingUtilities.invokeLater(this::refrescar);
        }
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 6));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JPanel informacion = new JPanel(new GridLayout(1, 2, 10, 4));
        informacion.add(etiquetaCodigo);
        informacion.add(etiquetaZona);
        add(informacion, BorderLayout.NORTH);

        JPanel estadisticas = new JPanel(new GridLayout(2, 3, 10, 4));
        estadisticas.add(new JLabel("Energía local"));
        estadisticas.add(new JLabel("Energía oponente"));
        estadisticas.add(new JLabel("Cafés"));
        estadisticas.add(barraVidaLocal);
        estadisticas.add(barraVidaOponente);
        estadisticas.add(etiquetaCafes);
        add(estadisticas, BorderLayout.CENTER);

        JPanel selectorZonas = new JPanel(new GridLayout(2, 0, 4, 4));
        for (int idZona = 0; idZona < Constants.TOTAL_ZONES; idZona++) {
            final int zona = idZona;
            JButton boton = new JButton(Integer.toString(idZona));
            boton.setToolTipText("Mostrar la zona " + idZona);
            boton.addActionListener(event -> controlador.verZona(zona));
            botonesZona.add(boton);
            selectorZonas.add(boton);
        }
        add(selectorZonas, BorderLayout.SOUTH);
    }

    private void refrescar() {
        Jugador local = model.getJugadorLocal();
        Jugador oponente = model.getOponente();
        configurarBarra(barraVidaLocal, local, "Local");
        configurarBarra(barraVidaOponente, oponente, "Oponente");

        int cafes = local == null ? 0 : local.getCafesConsumidos();
        etiquetaCafes.setText(cafes + " / " + Constants.TOTAL_COFFEE_CUPS);
        String codigo = model.getCodigoPartida();
        etiquetaCodigo.setText("Partida: " + (codigo == null ? "—" : codigo));
        int zonaVisible = model.getZonaActualVisible();
        etiquetaZona.setText("Zona: " + zonaVisible);
        for (int i = 0; i < botonesZona.size(); i++) {
            botonesZona.get(i).setEnabled(i != zonaVisible);
        }
    }

    private void configurarBarra(JProgressBar barra, Jugador jugador, String nombre) {
        int energia = jugador == null ? 0 : Math.max(0, Math.min(energiaMaxima(), jugador.getEnergiaTotal()));
        barra.setValue(energia);
        barra.setStringPainted(true);
        barra.setString(jugador == null
                ? nombre + ": —"
                : nombre + ": " + energia + " (" + jugador.getVivos() + " vivos)");
    }

    private static int energiaMaxima() {
        return Constants.MAX_PLAYER_ENERGY * Constants.PLAYER_PACK_SIZE;
    }
}
