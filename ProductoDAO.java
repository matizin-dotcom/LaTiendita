package com.discount3a.dao;

import com.discount3a.modelo.Producto;
import java.util.List;

/**
 * Acceso a datos de productos.
 */
public interface ProductoDAO {
    void guardar(List<Producto> productos);

    List<Producto> cargar();
}
