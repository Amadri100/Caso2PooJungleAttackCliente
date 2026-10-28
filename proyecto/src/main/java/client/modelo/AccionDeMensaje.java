package client.modelo;

import client.mensajes.Mensaje;
import client.mensajes.MensajeAnimalSelection;
import client.mensajes.MensajeCaso;
import client.mensajes.MensajeGameOver;
import client.mensajes.MensajeGameStarted;
import client.mensajes.MensajeMatchCreated;
import client.mensajes.MensajeMatchJoined;
import client.mensajes.MensajePrimerMensaje;
import client.mensajes.MensajeStateUpdate;

public class AccionDeMensaje {

    public static AccionMensaje obtenerAccion(Mensaje msg) {
        return switch (msg.getTipo()) {
            case CASO -> new AccionError((MensajeCaso) msg);
            case PRIMER_MENSAJE -> new AccionPrimerMensaje((MensajePrimerMensaje) msg);
            case MATCH_CREATED -> new AccionMatchCreated((MensajeMatchCreated) msg);
            case MATCH_JOINED -> new AccionMatchJoined((MensajeMatchJoined) msg);
            case ANIMAL_SELECTION -> new AccionAnimalSelection((MensajeAnimalSelection) msg);
            case GAME_STARTED -> new AccionGameStarted((MensajeGameStarted) msg);
            case STATE_UPDATE -> new AccionStateUpdate((MensajeStateUpdate) msg);
            case GAME_OVER -> new AccionGameOver((MensajeGameOver) msg);
            case INDICAR_NOMBRE, CREATE_MATCH, JOIN_MATCH, SELECT_ANIMAL,
                    MOVE_ANIMALS, ATTACK, VIEW_ZONE -> null;
        };
    }
}