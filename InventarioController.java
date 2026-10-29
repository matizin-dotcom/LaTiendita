package com.discount3a.controlador;

import com.discount3a.dao.CategoriaDAO;
import com.discount3a.dao.ProductoDAO;
import com.discount3a.dao.UbicacionDAO;
import com.discount3a.modelo.Producto;
import com.discount3a.structures.ArbolCategorias;
import com.discount3a.structures.InventarioVector;
import com.discount3a.structures.MatrizEstantes;
import com.discount3a.structures.NodoArbol;
import com.google.common.base.Preconditions;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla el inventario, las categorías y la distribución de estantes.
 */
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

    private final InventarioVector inventario = new InventarioVector();
    private final ArbolCategorias arbol = new ArbolCategorias();
    private final MatrizEstantes matriz = new MatrizEstantes();
    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;
    private final UbicacionDAO ubicacionDAO;

    public InventarioController(ProductoDAO productoDAO, CategoriaDAO categoriaDAO, UbicacionDAO ubicacionDAO) {
        this.productoDAO = Preconditions.checkNotNull(productoDAO, "productoDAO no puede ser nulo");
        this.categoriaDAO = Preconditions.checkNotNull(categoriaDAO, "categoriaDAO no puede ser nulo");
        this.ubicacionDAO = Preconditions.checkNotNull(ubicacionDAO, "ubicacionDAO no puede ser nulo");
    }

    /**
     * Carga categorías, productos y estantes desde los archivos txt.
     */
    public void cargar() {
        for (String[] relacion : categoriaDAO.cargar()) {
            String padre = relacion[0];
            String hijo = relacion[1];
            boolean ok = arbol.insertarCategoria(padre.isEmpty() ? null : padre, hijo);
            if (!ok) {
                logger.error("No se pudo recrear la categoría \"{}\"", hijo);
            }
        }
        for (Producto p : productoDAO.cargar()) {
            if (inventario.buscarPorCodigo(p.getCodigo()) != null) {
                logger.error("Código de producto duplicado omitido: {}", p.getCodigo());
                continue;
            }
            inventario.agregar(p);
            arbol.agregarProductoACategoria(p.getCategoria(), p);
        }
        ubicacionDAO.cargar(matriz);
        logger.info("Inventario cargado: {} productos", inventario.tamanio());
    }

    /**
     * Guarda categorías, productos y estantes en los archivos txt.
     */
    public void guardar() {
        productoDAO.guardar(inventario.listar());
        categoriaDAO.guardar(arbol.listarRelaciones());
        ubicacionDAO.guardar(matriz);
    }

    /**
     * Registra un producto nuevo. Lanza IllegalArgumentException si los datos no son válidos.
     */
    public Producto agregarProducto(String codigo, String nombre, String categoria, double precio, int stock) {
        Preconditions.checkArgument(inventario.buscarPorCodigo(codigo) == null,
                "Ya existe un producto con ese código.");
        Preconditions.checkArgument(arbol.existeCategoria(categoria),
                "La categoría \"%s\" no existe en el árbol.", categoria);
        Producto producto = new Producto(codigo, nombre, categoria, precio, stock);
        inventario.agregar(producto);
        arbol.agregarProductoACategoria(categoria, producto);
        logger.info("Producto registrado: {}", codigo);
        return producto;
    }

    public Producto buscarProducto(String codigo) {
        return inventario.buscarPorCodigo(codigo);
    }

    public List<Producto> listarProductos() {
        return inventario.listar();
    }

    public int cantidadProductos() {
        return inventario.tamanio();
    }

    public List<String> listarCategorias() {
        return arbol.listarTodasCategorias();
    }

    public boolean insertarCategoria(String padre, String nueva) {
        boolean ok = arbol.insertarCategoria(StringUtils.isBlank(padre) ? null : padre, nueva);
        if (ok) {
            logger.info("Categoría registrada: {}", nueva);
        }
        return ok;
    }

    public NodoArbol getRaizArbol() {
        return arbol.getRaiz();
    }

    /**
     * Asigna un producto a una celda de la matriz. Con código vacío se vacía la celda.
     * Devuelve false si la posición está fuera de rango.
     */
    public boolean asignarUbicacion(int pasillo, int nivel, String codigo) {
        if (StringUtils.isNotBlank(codigo)) {
            Preconditions.checkArgument(inventario.buscarPorCodigo(codigo) != null,
                    "No existe ningún producto con el código \"%s\" en el inventario.", codigo);
        }
        return matriz.asignar(pasillo, nivel, StringUtils.isBlank(codigo) ? null : codigo);
    }

    public String[][] getMatriz() {
        return matriz.getMatriz();
    }
}
