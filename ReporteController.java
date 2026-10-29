package com.discount3a.controlador;

import com.discount3a.dao.ReporteDAO;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;

/**
 * Genera el reporte general en texto y lo exporta a Excel.
 */
public class ReporteController {

    private final InventarioController inventarioController;
    private final VentaController ventaController;
    private final ClienteController clienteController;
    private final ReporteDAO reporteDAO;

    public ReporteController(InventarioController inventarioController, VentaController ventaController,
                             ClienteController clienteController, ReporteDAO reporteDAO) {
        this.inventarioController = Preconditions.checkNotNull(inventarioController, "inventarioController no puede ser nulo");
        this.ventaController = Preconditions.checkNotNull(ventaController, "ventaController no puede ser nulo");
        this.clienteController = Preconditions.checkNotNull(clienteController, "clienteController no puede ser nulo");
        this.reporteDAO = Preconditions.checkNotNull(reporteDAO, "reporteDAO no puede ser nulo");
    }

    /**
     * Arma el texto del reporte general que se muestra en pantalla.
     */
    public String generarTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE GENERAL - LA TIENDITA ===\n\n");
        sb.append("Productos en inventario: ").append(inventarioController.cantidadProductos()).append("\n");
        sb.append("Clientes en cola de espera: ").append(clienteController.tamanioCola()).append("\n");
        sb.append("Clientes frecuentes registrados: ").append(clienteController.tamanioFrecuentes()).append("\n");
        sb.append("Ventas realizadas: ").append(ventaController.cantidadVentas()).append("\n\n");
        sb.append(String.format("Ingresos totales por ventas: S/ %.2f\n", ventaController.calcularIngresos()));
        return sb.toString();
    }

    /**
     * Exporta el reporte general a un archivo .xlsx.
     */
    public void exportarExcel(File destino) throws IOException {
        Preconditions.checkNotNull(destino, "El archivo de destino no puede ser nulo");
        reporteDAO.exportar(destino,
                inventarioController.listarProductos(),
                ventaController.listarHistorial(),
                clienteController.tamanioCola(),
                clienteController.tamanioFrecuentes());
    }
}
