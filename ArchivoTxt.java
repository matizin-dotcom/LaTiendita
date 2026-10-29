package com.discount3a.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Lectura y escritura de archivos txt dentro de la carpeta de datos.
 */
public final class ArchivoTxt {

    public static final String SEP = "|";
    public static final String SEP_REGEX = "\\|";

    private ArchivoTxt() { }

    public static File archivo(String nombre) {
        return Rutas.carpetaDatos().resolve(nombre).toFile();
    }

    /**
     * Lee todas las líneas del archivo. Si no existe devuelve una lista vacía.
     */
    public static List<String> leerLineas(String nombre) throws IOException {
        File archivo = archivo(nombre);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        return FileUtils.readLines(archivo, StandardCharsets.UTF_8);
    }

    /**
     * Reemplaza el contenido del archivo por las líneas indicadas.
     */
    public static void escribirLineas(String nombre, List<String> lineas) throws IOException {
        FileUtils.writeLines(archivo(nombre), "UTF-8", lineas);
    }

    /**
     * Quita del texto el separador y los saltos de línea para no romper el formato.
     */
    public static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(SEP, "/").replace("\n", " ").replace("\r", " ").trim();
    }
}
