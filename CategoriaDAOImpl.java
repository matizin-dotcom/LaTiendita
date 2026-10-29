package com.discount3a.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guarda y lee las categorías en categorias.txt.
 */
public class CategoriaDAOImpl implements CategoriaDAO {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaDAOImpl.class);
    private static final String ARCHIVO = "categorias.txt";

    @Override
    public void guardar(List<String[]> relaciones) {
        List<String> lineas = new ArrayList<>();
        for (String[] relacion : relaciones) {
            lineas.add(ArchivoTxt.limpiar(relacion[0]) + ArchivoTxt.SEP + ArchivoTxt.limpiar(relacion[1]));
        }
        try {
            ArchivoTxt.escribirLineas(ARCHIVO, lineas);
        } catch (IOException e) {
            logger.error("No se pudo guardar {}", ARCHIVO, e);
        }
    }

    @Override
    public List<String[]> cargar() {
        List<String[]> relaciones = new ArrayList<>();
        List<String> lineas;
        try {
            lineas = ArchivoTxt.leerLineas(ARCHIVO);
        } catch (IOException e) {
            logger.error("No se pudo leer {}", ARCHIVO, e);
            return relaciones;
        }
        for (String linea : lineas) {
            if (StringUtils.isBlank(linea)) {
                continue;
            }
            try {
                String[] p = linea.split(ArchivoTxt.SEP_REGEX, -1);
                String padre = p[0].trim();
                String hijo = p[1].trim();
                if (hijo.isEmpty()) {
                    throw new IllegalArgumentException("categoría sin nombre");
                }
                relaciones.add(new String[]{padre, hijo});
            } catch (Exception e) {
                logger.error("Línea corrupta en {}: \"{}\"", ARCHIVO, linea, e);
            }
        }
        return relaciones;
    }
}
