package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;

import client.controlador.GameController;
import client.modelo.GameModel;
import client.red.MessageDispatcher;
import client.vista.MainWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            MessageDispatcher dispatcher = new MessageDispatcher(model);
            GameController controlador = new GameController(model, dispatcher);
            MainWindow ventana = new MainWindow(controlador);
            ventana.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    controlador.desconectar();
                }
            });
            ventana.setVisible(true);
        });
    }
}
