package com.discount3a.dao;

import com.discount3a.modelo.Producto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guarda y lee productos en productos.txt.
 */
public class ProductoDAOImpl implements ProductoDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProductoDAOImpl.class);
    private static final String ARCHIVO = "productos.txt";

    @Override
    public void guardar(List<Producto> productos) {
        List<String> lineas = new ArrayList<>();
        for (Producto p : productos) {
            lineas.add(String.join(ArchivoTxt.SEP,
                    ArchivoTxt.limpiar(p.getCodigo()),
                    ArchivoTxt.limpiar(p.getNombre()),
                    ArchivoTxt.limpiar(p.getCategoria()),
                    String.valueOf(p.getPrecio()),
                    String.valueOf(p.getStock())));
        }
        try {
            ArchivoTxt.escribirLineas(ARCHIVO, lineas);
        } catch (IOException e) {
            logger.error("No se pudo guardar {}", ARCHIVO, e);
        }
    }

    @Override
    public List<Producto> cargar() {
        List<Producto> productos = new ArrayList<>();
        List<String> lineas;
        try {
            lineas = ArchivoTxt.leerLineas(ARCHIVO);
        } catch (IOException e) {
            logger.error("No se pudo leer {}", ARCHIVO, e);
            return productos;
        }
        for (String linea : lineas) {
            if (StringUtils.isBlank(linea)) {
                continue;
            }
            try {
                String[] p = linea.split(ArchivoTxt.SEP_REGEX, -1);
                String codigo = p[0].trim();
                String nombre = p[1].trim();
                String categoria = p[2].trim();
                double precio = Double.parseDouble(p[3].trim());
                int stock = Integer.parseInt(p[4].trim());
                productos.add(new Producto(codigo, nombre, categoria, precio, stock));
            } catch (Exception e) {
                logger.error("Línea corrupta en {}: \"{}\"", ARCHIVO, linea, e);
            }
        }
        return productos;
    }
}
