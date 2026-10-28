package client.constantes;

public final class Constants {
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 4001;
    public static final int LONGITUD_CODIGO = 4;
    public static final String NOMBRE_UNIVERSAL = "@Todos";
    public static final int MAX_PLAYER_ENERGY = 20;
    public static final int PLAYER_PACK_SIZE = 10;
    public static final int TOTAL_ZONES = 12;
    public static final int TOTAL_COFFEE_CUPS = 10;
    public static final double ENCOUNTER_RADIUS = 50.0;
    public static final String ERROR_JSON_VACIO = "El JSON está vacío";
    public static final String CAMPO_TIPO = "tipo";
    public static final String ERROR_TIPO_AUSENTE = "El mensaje no tiene el campo 'tipo'";
    public static final String ERROR_TIPO_DESCONOCIDO = "Tipo de mensaje desconocido";
    public static final String ERROR_CLASE_NULA = "La clase destino no puede ser null";
    public static final String ERROR_DESERIALIZACION = "No se pudo serializar el mensaje";
    public static final String ERROR_MENSAJE_NULO = "El mensaje no puede ser null";
    public static final String ERROR_SERIALIZACION = "No se pudo serializar el mensaje";

    private Constants() {
    }
}
