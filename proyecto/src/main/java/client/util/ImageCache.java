package client.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import client.modelo.TipoEspecie;

/**
 * Cache global de imagenes y texturas para no leer disco repetidamente.
 * Las imagenes se buscan en el classpath, dentro de RUTA_BASE.
 */
public class ImageCache {

    private static final System.Logger LOGGER = System.getLogger(ImageCache.class.getName());
    private static final String RUTA_BASE = "/imagenes/";

    private static ImageCache instancia;

    private final Map<String, BufferedImage> imagenes;

    private ImageCache() {
        this.imagenes = new ConcurrentHashMap<>();
    }

    public static synchronized ImageCache getInstance() {
        if (instancia == null) {
            instancia = new ImageCache();
        }
        return instancia;
    }

    /**
     * Carga por adelantado las imagenes de cada especie
     * (clave: nombre de la especie en minusculas + ".png").
     */
    public void precargar() {
        for (TipoEspecie especie : TipoEspecie.values()) {
            if (especie != TipoEspecie.NINGUNA) {
                getImagen(especie.name().toLowerCase() + ".png");
            }
        }
    }

    /**
     * Devuelve la imagen asociada a la clave (nombre de archivo, por ejemplo "jaguar.png").
     * La lee del disco solo la primera vez. Devuelve null si no se puede cargar.
     */
    public BufferedImage getImagen(String key) {
        if (key == null) {
            return null;
        }
        BufferedImage imagen = imagenes.get(key);
        if (imagen == null) {
            imagen = cargar(key);
            if (imagen != null) {
                imagenes.put(key, imagen);
            }
        }
        return imagen;
    }

    private BufferedImage cargar(String key) {
        try (InputStream in = ImageCache.class.getResourceAsStream(RUTA_BASE + key)) {
            if (in == null) {
                LOGGER.log(System.Logger.Level.WARNING, "No se encontro la imagen: {0}", key);
                return null;
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.WARNING, "No se pudo leer la imagen: " + key, e);
            return null;
        }
    }
}