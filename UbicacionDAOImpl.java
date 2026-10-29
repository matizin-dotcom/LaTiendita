package com.discount3a.dao;

import com.discount3a.structures.MatrizEstantes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guarda y lee la distribución de estantes en matriz.txt.
 */
public class UbicacionDAOImpl implements UbicacionDAO {

    private static final Logger logger = LoggerFactory.getLogger(UbicacionDAOImpl.class);
    private static final String ARCHIVO = "matriz.txt";

    @Override
    public void guardar(MatrizEstantes matriz) {
        List<String> lineas = new ArrayList<>();
        for (int i = 0; i < MatrizEstantes.PASILLOS; i++) {
            for (int j = 0; j < MatrizEstantes.NIVELES; j++) {
                String codigo = matriz.obtener(i, j);
                if (StringUtils.isNotBlank(codigo)) {
                    lineas.add(i + ArchivoTxt.SEP + j + ArchivoTxt.SEP + ArchivoTxt.limpiar(codigo));
                }
            }
        }
        try {
            ArchivoTxt.escribirLineas(ARCHIVO, lineas);
        } catch (IOException e) {
            logger.error("No se pudo guardar {}", ARCHIVO, e);
        }
    }

    @Override
    public void cargar(MatrizEstantes matriz) {
        List<String> lineas;
        try {
            lineas = ArchivoTxt.leerLineas(ARCHIVO);
        } catch (IOException e) {
            logger.error("No se pudo leer {}", ARCHIVO, e);
            return;
        }
        for (String linea : lineas) {
            if (StringUtils.isBlank(linea)) {
                continue;
            }
            try {
                String[] p = linea.split(ArchivoTxt.SEP_REGEX, -1);
                int pasillo = Integer.parseInt(p[0].trim());
                int nivel = Integer.parseInt(p[1].trim());
                String codigo = p[2].trim();
                if (!matriz.asignar(pasillo, nivel, codigo)) {
                    throw new IllegalArgumentException("posición fuera de rango");
                }
            } catch (Exception e) {
                logger.error("Línea corrupta en {}: \"{}\"", ARCHIVO, linea, e);
            }
        }
    }
}
