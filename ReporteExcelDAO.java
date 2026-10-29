package com.discount3a.dao;

import com.discount3a.modelo.Producto;
import com.discount3a.modelo.Venta;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exporta el reporte general a un archivo Excel (.xlsx) con Apache POI.
 */
public class ReporteExcelDAO implements ReporteDAO {

    private static final Logger logger = LoggerFactory.getLogger(ReporteExcelDAO.class);

    @Override
    public void exportar(File destino, List<Producto> productos, List<Venta> ventas,
                         int clientesEnCola, int clientesFrecuentes) throws IOException {
        try (Workbook libro = new XSSFWorkbook(); FileOutputStream salida = new FileOutputStream(destino)) {
            CellStyle negrita = crearEstiloNegrita(libro);

            double ingresos = 0;
            for (Venta v : ventas) {
                ingresos += v.getTotal();
            }

            Sheet resumen = libro.createSheet("Resumen");
            escribirFila(resumen, 0, negrita, "Concepto", "Valor");
            escribirFila(resumen, 1, null, "Productos en inventario", productos.size());
            escribirFila(resumen, 2, null, "Clientes en cola de espera", clientesEnCola);
            escribirFila(resumen, 3, null, "Clientes frecuentes registrados", clientesFrecuentes);
            escribirFila(resumen, 4, null, "Ventas realizadas", ventas.size());
            escribirFila(resumen, 5, null, "Ingresos totales (S/)", ingresos);
            resumen.setColumnWidth(0, 10000);
            resumen.setColumnWidth(1, 5000);

            Sheet inventario = libro.createSheet("Inventario");
            escribirFila(inventario, 0, negrita, "Código", "Nombre", "Categoría", "Precio (S/)", "Stock");
            int fila = 1;
            for (Producto p : productos) {
                escribirFila(inventario, fila, null, p.getCodigo(), p.getNombre(),
                        p.getCategoria(), p.getPrecio(), p.getStock());
                fila++;
            }
            for (int i = 0; i < 5; i++) {
                inventario.setColumnWidth(i, 5000);
            }

            Sheet hojaVentas = libro.createSheet("Ventas");
            escribirFila(hojaVentas, 0, negrita, "N° venta", "Cliente", "Fecha", "Total (S/)");
            fila = 1;
            for (Venta v : ventas) {
                escribirFila(hojaVentas, fila, null, v.getId(), v.getCliente(), v.getFechaTexto(), v.getTotal());
                fila++;
            }
            for (int i = 0; i < 4; i++) {
                hojaVentas.setColumnWidth(i, 5500);
            }

            libro.write(salida);
            logger.info("Reporte exportado a {}", destino.getAbsolutePath());
        }
    }

    private CellStyle crearEstiloNegrita(Workbook libro) {
        Font fuente = libro.createFont();
        fuente.setBold(true);
        CellStyle estilo = libro.createCellStyle();
        estilo.setFont(fuente);
        return estilo;
    }

    private void escribirFila(Sheet hoja, int numeroFila, CellStyle estilo, Object... valores) {
        Row fila = hoja.createRow(numeroFila);
        for (int i = 0; i < valores.length; i++) {
            Object valor = valores[i];
            if (valor instanceof Number) {
                fila.createCell(i).setCellValue(((Number) valor).doubleValue());
            } else {
                fila.createCell(i).setCellValue(String.valueOf(valor));
            }
            if (estilo != null) {
                fila.getCell(i).setCellStyle(estilo);
            }
        }
    }
}
