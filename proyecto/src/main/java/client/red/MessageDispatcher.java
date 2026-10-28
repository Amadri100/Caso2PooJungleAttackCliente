package client.red;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import client.mensajes.MensajeAnimalSelection;
import client.mensajes.MensajeCaso;
import client.mensajes.MensajeGameOver;
import client.mensajes.MensajeGameStarted;
import client.mensajes.MensajeMatchCreated;
import client.mensajes.MensajeMatchJoined;
import client.mensajes.MensajePrimerMensaje;
import client.mensajes.MensajeServidor;
import client.mensajes.MensajeStateUpdate;
import client.mensajes.TipoMensaje;
import client.modelo.AccionAnimalSelection;
import client.modelo.AccionError;
import client.modelo.AccionGameOver;
import client.modelo.AccionGameStarted;
import client.modelo.AccionMatchCreated;
import client.modelo.AccionMatchJoined;
import client.modelo.AccionPrimerMensaje;
import client.modelo.AccionStateUpdate;
import client.modelo.GameModel;

public class MessageDispatcher {
    private static final System.Logger LOGGER = System.getLogger(MessageDispatcher.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final CopyOnWriteArrayList<Consumer<MensajeServidor>> listeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Runnable> listenersDesconexion = new CopyOnWriteArrayList<>();

    public MessageDispatcher() {
    }

    public MessageDispatcher(GameModel model) {
        Objects.requireNonNull(model, "El modelo es obligatorio.");
        agregarListener(mensaje -> {
            switch (mensaje.getTipo()) {
                case CASO -> model.realizarAccion(new AccionError((MensajeCaso) mensaje));
                case PRIMER_MENSAJE -> model.realizarAccion(new AccionPrimerMensaje((MensajePrimerMensaje) mensaje));
                case MATCH_CREATED -> model.realizarAccion(new AccionMatchCreated((MensajeMatchCreated) mensaje));
                case MATCH_JOINED -> model.realizarAccion(new AccionMatchJoined((MensajeMatchJoined) mensaje));
                case ANIMAL_SELECTION -> model.realizarAccion(new AccionAnimalSelection((MensajeAnimalSelection) mensaje));
                case GAME_STARTED -> model.realizarAccion(new AccionGameStarted((MensajeGameStarted) mensaje));
                case STATE_UPDATE -> model.realizarAccion(new AccionStateUpdate((MensajeStateUpdate) mensaje));
                case GAME_OVER -> model.realizarAccion(new AccionGameOver((MensajeGameOver) mensaje));
                default -> { }
            }
        });
        agregarListenerDesconexion(model::onDesconexion);
    }

    public MessageDispatcher(Consumer<MensajeServidor> listener) {
        agregarListener(listener);
    }

    public void agregarListener(Consumer<MensajeServidor> listener) {
        listeners.add(Objects.requireNonNull(listener, "El listener es obligatorio."));
    }

    public void agregarListenerDesconexion(Runnable listener) {
        listenersDesconexion.add(Objects.requireNonNull(listener, "El listener es obligatorio."));
    }

    public void despachar(String json) {
        try {
            JsonNode raiz = MAPPER.readTree(json);
            if (raiz == null || !raiz.isObject()) {
                throw new IllegalArgumentException("El mensaje JSON debe ser un objeto.");
            }

            JsonNode tipoNodo = raiz.hasNonNull("tipo") ? raiz.get("tipo") : raiz.get("type");
            if (tipoNodo == null || !tipoNodo.isTextual()) {
                throw new IllegalArgumentException("El mensaje no contiene un tipo textual.");
            }
            TipoMensaje tipo;
            try {
                tipo = TipoMensaje.valueOf(tipoNodo.asText());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de mensaje desconocido: " + tipoNodo.asText(), e);
            }

            JsonNode payload = raiz.has("payload") ? raiz.get("payload") : raiz;
            if (payload == null || !payload.isObject()) {
                throw new IllegalArgumentException("El payload del mensaje debe ser un objeto.");
            }

            MensajeServidor mensaje = convertirMensaje(tipo, payload);
            if (mensaje == null) {
                LOGGER.log(System.Logger.Level.WARNING,
                        "Se ignoró un mensaje de cliente recibido desde el servidor: {0}", tipo);
                return;
            }
            if (listeners.isEmpty()) {
                LOGGER.log(System.Logger.Level.WARNING,
                        "Mensaje {0} recibido sin listeners registrados.", tipo);
                return;
            }
            for (Consumer<MensajeServidor> listener : listeners) {
                listener.accept(mensaje);
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            LOGGER.log(System.Logger.Level.WARNING, "No se pudo procesar el mensaje recibido.", e);
        }
    }

    public void conexionPerdida() {
        for (Runnable listener : listenersDesconexion) {
            listener.run();
        }
    }

    private MensajeServidor convertirMensaje(TipoMensaje tipo, JsonNode payload)
            throws JsonProcessingException {
        return switch (tipo) {
            case CASO -> MAPPER.treeToValue(payload, MensajeCaso.class);
            case PRIMER_MENSAJE -> MAPPER.treeToValue(payload, MensajePrimerMensaje.class);
            case MATCH_CREATED -> MAPPER.treeToValue(payload, MensajeMatchCreated.class);
            case MATCH_JOINED -> MAPPER.treeToValue(payload, MensajeMatchJoined.class);
            case ANIMAL_SELECTION -> MAPPER.treeToValue(payload, MensajeAnimalSelection.class);
            case GAME_STARTED -> MAPPER.treeToValue(payload, MensajeGameStarted.class);
            case STATE_UPDATE -> MAPPER.treeToValue(payload, MensajeStateUpdate.class);
            case GAME_OVER -> MAPPER.treeToValue(payload, MensajeGameOver.class);
            case INDICAR_NOMBRE, CREATE_MATCH, JOIN_MATCH, SELECT_ANIMAL,
                    MOVE_ANIMALS, ATTACK, VIEW_ZONE -> null;
        };
    }
}