package com.discount3a.dao;

import com.discount3a.modelo.Producto;
import com.discount3a.modelo.Venta;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Exportación del reporte general.
 */
public interface ReporteDAO {
    void exportar(File destino, List<Producto> productos, List<Venta> ventas,
                  int clientesEnCola, int clientesFrecuentes) throws IOException;
}
