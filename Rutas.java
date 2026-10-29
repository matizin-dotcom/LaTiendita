package com.discount3a.dao;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ubica la carpeta "datos3A" donde se guardan los archivos txt.
 */
public final class Rutas {

    private static final Logger logger = LoggerFactory.getLogger(Rutas.class);
    private static final String NOMBRE_CARPETA = "datos3A";

    private Rutas() { }

    /**
     * Devuelve la carpeta de datos y la crea si no existe.
     */
    public static Path carpetaDatos() {
        Path base = resolverCarpetaDelEjecutable();
        Path carpeta = base.resolve(NOMBRE_CARPETA);
        try {
            Files.createDirectories(carpeta);
            return carpeta;
        } catch (Exception e) {
            logger.error("No se pudo crear la carpeta {}", carpeta, e);
            try {
                Path alterna = Paths.get(System.getProperty("user.dir", "."), NOMBRE_CARPETA);
                Files.createDirectories(alterna);
                return alterna;
            } catch (Exception e2) {
                logger.error("No se pudo crear la carpeta alterna de datos", e2);
                return Paths.get(".");
            }
        }
    }

    private static Path resolverCarpetaDelEjecutable() {
        try {
            File origen = new File(Rutas.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            return origen.isFile() ? origen.getParentFile().toPath() : origen.toPath();
        } catch (URISyntaxException | NullPointerException | SecurityException e) {
            logger.error("No se pudo ubicar el ejecutable, se usa el directorio actual", e);
            return Paths.get(System.getProperty("user.dir", "."));
        }
    }
}
