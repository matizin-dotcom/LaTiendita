package com.discount3a.dao;

import com.discount3a.modelo.Cliente;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guarda y lee clientes en el archivo txt indicado (cola o frecuentes).
 */
public class ClienteDAOImpl implements ClienteDAO {

    private static final Logger logger = LoggerFactory.getLogger(ClienteDAOImpl.class);

    private final String archivo;

    public ClienteDAOImpl(String archivo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(archivo), "El nombre del archivo no puede estar vacío");
        this.archivo = archivo;
    }

    @Override
    public void guardar(List<Cliente> clientes) {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) {
            lineas.add(c.getNumero() + ArchivoTxt.SEP + ArchivoTxt.limpiar(c.getNombre()));
        }
        try {
            ArchivoTxt.escribirLineas(archivo, lineas);
        } catch (IOException e) {
            logger.error("No se pudo guardar {}", archivo, e);
        }
    }

    @Override
    public List<Cliente> cargar() {
        List<Cliente> clientes = new ArrayList<>();
        List<String> lineas;
        try {
            lineas = ArchivoTxt.leerLineas(archivo);
        } catch (IOException e) {
            logger.error("No se pudo leer {}", archivo, e);
            return clientes;
        }
        for (String linea : lineas) {
            if (StringUtils.isBlank(linea)) {
                continue;
            }
            try {
                String[] p = linea.split(ArchivoTxt.SEP_REGEX, -1);
                int numero = Integer.parseInt(p[0].trim());
                String nombre = p[1].trim();
                clientes.add(new Cliente(numero, nombre));
            } catch (Exception e) {
                logger.error("Línea corrupta en {}: \"{}\"", archivo, linea, e);
            }
        }
        return clientes;
    }
}
