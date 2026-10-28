package client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import client.constantes.Constants;
import client.mensajes.*;
 

public final class Json {

    //Indica que no falle si no conoce alguna propiedad; permitiendo herencia.
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Json() {
    }

    // Serializa cualquier Mensaje (o subclase) con todos sus campos
    public static String mensajeAJson(Mensaje msg) {
        if (msg == null) {
            throw new IllegalArgumentException(Constants.ERROR_MENSAJE_NULO);
        }
        try {
            return MAPPER.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    Constants.ERROR_SERIALIZACION + ": " + e.getOriginalMessage(), e);
        }
    }

    // Lee solo el tipo del JSON y devuelve un Mensaje base
    public static Mensaje jsonAMensaje(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException(Constants.ERROR_JSON_VACIO);
        }
        try {
            JsonNode nodoTipo = MAPPER.readTree(json).get(Constants.CAMPO_TIPO);
            if (nodoTipo == null || nodoTipo.isNull()) {
                throw new IllegalArgumentException(Constants.ERROR_TIPO_AUSENTE);
            }
            try {
                return new Mensaje(TipoMensaje.valueOf(nodoTipo.asText()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        Constants.ERROR_TIPO_DESCONOCIDO + ": " + nodoTipo.asText(), e);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    Constants.ERROR_DESERIALIZACION + ": " + e.getOriginalMessage(), e);
        }
    }
 
    public static Mensaje jsonA(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException(Constants.ERROR_JSON_VACIO);
        }
        try {
            return MAPPER.readValue(json, Mensaje.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    Constants.ERROR_DESERIALIZACION + ": " + e.getOriginalMessage(), e);
        }
    }
}