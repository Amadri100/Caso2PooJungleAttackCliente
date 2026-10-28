package client.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.constantes.NetworkConstants;
import client.controlador.GameController;
import client.mensajes.TipoMensaje;
import client.modelo.GameModel;
import client.modelo.TipoEspecie;
import client.util.Observer;

public class MainWindow extends JFrame implements Observer {
    public static final String PANTALLA_MENU = "menu";
    public static final String PANTALLA_SELECCION = "seleccion";
    public static final String PANTALLA_JUEGO = "juego";

    private final GameController controlador;
    private final GameModel model;
    private final GamePanel gamePanel;
    private final HUDPanel hudPanel;
    private JComboBox<TipoEspecie> selectorEspecies;
    private final CardLayout layout = new CardLayout();
    private final JPanel contenedor = new JPanel(layout);
    private final JLabel estadoConexion = new JLabel("Desconectado");

    public MainWindow(GameController controlador) {
        super("Jungle Attack");
        this.controlador = Objects.requireNonNull(controlador, "El controlador es obligatorio.");
        this.model = controlador.getModel();
        this.gamePanel = new GamePanel(model, controlador);
        this.hudPanel = new HUDPanel(model, controlador);

        model.agregarObserver(this);
        model.agregarObserver(hudPanel);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setSize(1100, 740);
        setLocationRelativeTo(null);

        contenedor.add(mostrarMenuPrincipal(), PANTALLA_MENU);
        contenedor.add(mostrarSeleccionEspecie(), PANTALLA_SELECCION);
        contenedor.add(crearPantallaJuego(), PANTALLA_JUEGO);
        setContentPane(contenedor);
        mostrarPantalla(PANTALLA_MENU);
    }

    public void mostrarPantalla(String panelName) {
        if (!PANTALLA_MENU.equals(panelName)
                && !PANTALLA_SELECCION.equals(panelName)
                && !PANTALLA_JUEGO.equals(panelName)) {
            throw new IllegalArgumentException("Pantalla desconocida: " + panelName);
        }
        layout.show(contenedor, panelName);
    }

    @Override
    public void update(TipoMensaje tipo) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> update(tipo));
            return;
        }

        estadoConexion.setText(model.isNombreConfirmado() ? "Conectado" : "Desconectado");
        switch (tipo) {
            case GAME_STARTED, STATE_UPDATE -> {
                mostrarPantalla(PANTALLA_JUEGO);
                gamePanel.repaint();
            }
            case ANIMAL_SELECTION -> {
                actualizarEspeciesDisponibles();
                mostrarPantalla(PANTALLA_SELECCION);
            }
            case GAME_OVER -> {
                mostrarMensaje("Partida finalizada. Ganador: " + model.getGanador());
                mostrarPantalla(PANTALLA_MENU);
            }
            case CASO -> {
                if (model.getUltimoError() != null) {
                    mostrarMensaje(model.getUltimoError());
                }
            }
            default -> {
            }
        }
    }

    private JPanel mostrarMenuPrincipal() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Jungle Attack");
        titulo.setFont(titulo.getFont().deriveFont(30f));
        JButton conectar = new JButton("Conectar");
        JButton crearPartida = new JButton("Crear partida");
        JButton unirsePartida = new JButton("Unirse a partida");
        JButton seleccionarEspecie = new JButton("Seleccionar especie");
        JButton desconectar = new JButton("Desconectar");

        conectar.addActionListener(event -> pedirConexion());
        crearPartida.addActionListener(event -> {
            if (requiereConexion()) {
                controlador.crearPartida();
            }
        });
        unirsePartida.addActionListener(event -> {
            if (requiereConexion()) {
                String codigo = JOptionPane.showInputDialog(this, "Código de partida:");
                if (codigo != null) {
                    controlador.unirsePartida(codigo);
                }
            }
        });
        seleccionarEspecie.addActionListener(event -> mostrarPantalla(PANTALLA_SELECCION));
        desconectar.addActionListener(event -> controlador.desconectar());

        int fila = 0;
        panel.add(titulo, constraints(fila++));
        panel.add(estadoConexion, constraints(fila++));
        panel.add(conectar, constraints(fila++));
        panel.add(crearPartida, constraints(fila++));
        panel.add(unirsePartida, constraints(fila++));
        panel.add(seleccionarEspecie, constraints(fila++));
        panel.add(desconectar, constraints(fila));
        return panel;
    }

    private JPanel mostrarSeleccionEspecie() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        selectorEspecies = new JComboBox<>(
                model.getEspeciesDisponibles().stream()
                        .filter(especie -> especie != TipoEspecie.NINGUNA)
                        .toArray(TipoEspecie[]::new));
        JButton confirmar = new JButton("Elegir");
        JButton volver = new JButton("Volver");

        confirmar.addActionListener(event -> {
            TipoEspecie especie = (TipoEspecie) selectorEspecies.getSelectedItem();
            if (especie == null) {
                mostrarMensaje("El servidor aún no ha enviado especies disponibles.");
                return;
            }
            if (requiereConexion()) {
                controlador.seleccionarEspecie(especie);
            }
        });
        volver.addActionListener(event -> mostrarPantalla(PANTALLA_MENU));

        panel.add(new JLabel("Especie:"), constraints(0));
        panel.add(selectorEspecies, constraints(1));
        panel.add(confirmar, constraints(2));
        panel.add(volver, constraints(3));
        return panel;
    }

    private void actualizarEspeciesDisponibles() {
        TipoEspecie seleccionada = (TipoEspecie) selectorEspecies.getSelectedItem();
        selectorEspecies.removeAllItems();
        for (TipoEspecie especie : model.getEspeciesDisponibles()) {
            if (especie != TipoEspecie.NINGUNA) {
                selectorEspecies.addItem(especie);
            }
        }
        if (seleccionada != null) {
            selectorEspecies.setSelectedItem(seleccionada);
        }
    }

    private JPanel crearPantallaJuego() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(hudPanel, BorderLayout.NORTH);
        panel.add(gamePanel, BorderLayout.CENTER);

        JButton volver = new JButton("Menú");
        volver.addActionListener(event -> mostrarPantalla(PANTALLA_MENU));
        panel.add(volver, BorderLayout.SOUTH);
        return panel;
    }

    private void pedirConexion() {
        JTextField nickname = new JTextField(model.getNickname() == null ? "" : model.getNickname(), 16);
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.add(new JLabel("Nombre:"), constraints(0));
        formulario.add(nickname, constraints(1));
        int opcion = JOptionPane.showConfirmDialog(
                this, formulario, "Conectar al servidor", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            boolean conecto = controlador.conectar(
                    NetworkConstants.DEFAULT_HOST, NetworkConstants.DEFAULT_PORT, nickname.getText());
            if (!conecto && model.getUltimoError() != null) {
                mostrarMensaje(model.getUltimoError());
            }
        }
    }

    private boolean requiereConexion() {
        if (model.isNombreConfirmado()) {
            return true;
        }
        mostrarMensaje("Conéctate al servidor antes de realizar esta acción.");
        return false;
    }

    private GridBagConstraints constraints(int fila) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = fila;
        constraints.insets = new Insets(8, 8, 8, 8);
        return constraints;
    }

    private void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Jungle Attack", JOptionPane.INFORMATION_MESSAGE);
    }
}
