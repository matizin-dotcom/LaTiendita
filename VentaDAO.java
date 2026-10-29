package com.discount3a.dao;

import com.discount3a.modelo.Producto;
import com.discount3a.modelo.Venta;
import java.util.List;

/**
 * Acceso a datos de ventas.
 */
public interface VentaDAO {
    void guardar(List<Venta> ventas);

    List<Venta> cargar(List<Producto> productos);
}
