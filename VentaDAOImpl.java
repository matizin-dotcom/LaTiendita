package com.discount3a.dao;

import com.discount3a.modelo.ItemVenta;
import com.discount3a.modelo.Producto;
import com.discount3a.modelo.Venta;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guarda y lee el historial de ventas en historial.txt.
 */
public class VentaDAOImpl implements VentaDAO {

    private static final Logger logger = LoggerFactory.getLogger(VentaDAOImpl.class);
    private static final String ARCHIVO = "historial.txt";

    @Override
    public void guardar(List<Venta> ventas) {
        List<String> lineas = new ArrayList<>();
        for (Venta v : ventas) {
            lineas.add(String.join(ArchivoTxt.SEP, "VENTA", String.valueOf(v.getId()),
                    ArchivoTxt.limpiar(v.getCliente()), v.getFecha().toString(),
                    String.valueOf(v.getTotal())));
            for (ItemVenta it : v.getItems()) {
                lineas.add(String.join(ArchivoTxt.SEP, "ITEM",
                        ArchivoTxt.limpiar(it.getProducto().getCodigo()),
                        ArchivoTxt.limpiar(it.getProducto().getNombre()),
                        String.valueOf(it.getCantidad()),
                        String.valueOf(it.getProducto().getPrecio())));
            }
            lineas.add("");
        }
        try {
            ArchivoTxt.escribirLineas(ARCHIVO, lineas);
        } catch (IOException e) {
            logger.error("No se pudo guardar {}", ARCHIVO, e);
        }
    }

    @Override
    public List<Venta> cargar(List<Producto> productos) {
        List<Venta> ventas = new ArrayList<>();
        List<String> lineas;
        try {
            lineas = ArchivoTxt.leerLineas(ARCHIVO);
        } catch (IOException e) {
            logger.error("No se pudo leer {}", ARCHIVO, e);
            return ventas;
        }

        int id = 0;
        String cliente = "";
        LocalDateTime fecha = null;
        List<ItemVenta> items = new ArrayList<>();
        boolean ventaValida = false;

        for (String linea : lineas) {
            if (StringUtils.isBlank(linea)) {
                continue;
            }
            try {
                String[] p = linea.split(ArchivoTxt.SEP_REGEX, -1);
                if ("VENTA".equals(p[0])) {
                    if (ventaValida) {
                        agregarVenta(ventas, id, cliente, fecha, items);
                    }
                    ventaValida = false;
                    items = new ArrayList<>();
                    id = Integer.parseInt(p[1].trim());
                    cliente = p[2].trim();
                    fecha = LocalDateTime.parse(p[3].trim());
                    ventaValida = true;
                } else if ("ITEM".equals(p[0])) {
                    if (ventaValida) {
                        items.add(crearItem(p, productos));
                    }
                } else {
                    throw new IllegalArgumentException("tipo de línea desconocido");
                }
            } catch (Exception e) {
                logger.error("Línea corrupta en {}: \"{}\"", ARCHIVO, linea, e);
            }
        }
        if (ventaValida) {
            agregarVenta(ventas, id, cliente, fecha, items);
        }
        return ventas;
    }

    private void agregarVenta(List<Venta> ventas, int id, String cliente,
                              LocalDateTime fecha, List<ItemVenta> items) {
        try {
            ventas.add(new Venta(id, cliente, items, fecha));
        } catch (Exception e) {
            logger.error("Venta #{} omitida por datos inválidos", id, e);
        }
    }

    private ItemVenta crearItem(String[] p, List<Producto> productos) {
        String codigo = p[1].trim();
        String nombre = p[2].trim();
        int cantidad = Integer.parseInt(p[3].trim());
        double precio = Double.parseDouble(p[4].trim());
        Producto producto = buscarProducto(productos, codigo);
        if (producto == null) {
            producto = new Producto(codigo, nombre, "Desconocida", precio, 0);
        }
        return new ItemVenta(producto, cantidad);
    }

    private Producto buscarProducto(List<Producto> productos, String codigo) {
        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }
}
