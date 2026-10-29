package com.discount3a.controlador;

import com.discount3a.dao.VentaDAO;
import com.discount3a.modelo.ItemVenta;
import com.discount3a.modelo.Venta;
import com.discount3a.structures.PilaHistorialVentas;
import com.google.common.base.Preconditions;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla el registro de ventas y el historial.
 */
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final PilaHistorialVentas historial = new PilaHistorialVentas();
    private final InventarioController inventarioController;
    private final VentaDAO ventaDAO;

    public VentaController(InventarioController inventarioController, VentaDAO ventaDAO) {
        this.inventarioController = Preconditions.checkNotNull(inventarioController, "inventarioController no puede ser nulo");
        this.ventaDAO = Preconditions.checkNotNull(ventaDAO, "ventaDAO no puede ser nulo");
    }

    /**
     * Carga el historial desde el txt. Debe llamarse después de cargar el inventario.
     */
    public void cargar() {
        List<Venta> ventas = ventaDAO.cargar(inventarioController.listarProductos());
        for (int i = ventas.size() - 1; i >= 0; i--) {
            historial.registrarVenta(ventas.get(i));
        }
        logger.info("Historial cargado: {} ventas", historial.tamanio());
    }

    public void guardar() {
        ventaDAO.guardar(historial.verHistorial());
    }

    /**
     * Registra la venta, descuenta el stock y la apila en el historial.
     */
    public Venta registrarVenta(String cliente, List<ItemVenta> items) {
        Venta venta = new Venta(cliente, items);
        for (ItemVenta item : items) {
            item.getProducto().reducirStock(item.getCantidad());
        }
        historial.registrarVenta(venta);
        logger.info("Venta registrada: #{} por S/ {}", venta.getId(), String.format("%.2f", venta.getTotal()));
        return venta;
    }

    /**
     * Deshace la última venta y devuelve el stock. Devuelve null si no hay ventas.
     */
    public Venta deshacerUltimaVenta() {
        Venta venta = historial.deshacerUltimaVenta();
        if (venta == null) {
            return null;
        }
        for (ItemVenta item : venta.getItems()) {
            item.getProducto().aumentarStock(item.getCantidad());
        }
        logger.info("Venta deshecha: #{}", venta.getId());
        return venta;
    }

    public List<Venta> listarHistorial() {
        return historial.verHistorial();
    }

    public int cantidadVentas() {
        return historial.tamanio();
    }

    public double calcularIngresos() {
        double total = 0;
        for (Venta v : historial.verHistorial()) {
            total += v.getTotal();
        }
        return total;
    }
}
