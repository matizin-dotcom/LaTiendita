package com.discount3a.controlador;

import com.discount3a.dao.ClienteDAO;
import com.discount3a.modelo.Cliente;
import com.discount3a.structures.ColaClientes;
import com.discount3a.structures.ListaClientesFrecuentes;
import com.google.common.base.Preconditions;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla la cola de caja y los clientes frecuentes.
 */
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ColaClientes cola = new ColaClientes();
    private final ListaClientesFrecuentes frecuentes = new ListaClientesFrecuentes();
    private final ClienteDAO colaDAO;
    private final ClienteDAO frecuentesDAO;

    public ClienteController(ClienteDAO colaDAO, ClienteDAO frecuentesDAO) {
        this.colaDAO = Preconditions.checkNotNull(colaDAO, "colaDAO no puede ser nulo");
        this.frecuentesDAO = Preconditions.checkNotNull(frecuentesDAO, "frecuentesDAO no puede ser nulo");
    }

    public void cargar() {
        for (Cliente c : colaDAO.cargar()) {
            cola.encolar(c);
        }
        for (Cliente c : frecuentesDAO.cargar()) {
            frecuentes.agregar(c);
        }
        logger.info("Clientes cargados: {} en cola, {} frecuentes", cola.tamanio(), frecuentes.tamanio());
    }

    public void guardar() {
        colaDAO.guardar(cola.verCola());
        frecuentesDAO.guardar(frecuentes.listar());
    }

    public Cliente encolar(String nombre) {
        Cliente cliente = new Cliente(nombre);
        cola.encolar(cliente);
        logger.info("Cliente encolado: {}", cliente);
        return cliente;
    }

    public Cliente atenderSiguiente() {
        Cliente cliente = cola.atenderSiguiente();
        if (cliente != null) {
            logger.info("Cliente atendido: {}", cliente);
        }
        return cliente;
    }

    public List<Cliente> listarCola() {
        return cola.verCola();
    }

    public int tamanioCola() {
        return cola.tamanio();
    }

    public Cliente agregarFrecuente(String nombre) {
        Cliente cliente = new Cliente(nombre);
        frecuentes.agregar(cliente);
        logger.info("Cliente frecuente agregado: {}", cliente);
        return cliente;
    }

    public boolean eliminarFrecuente(int numero) {
        return frecuentes.eliminar(numero);
    }

    public List<Cliente> listarFrecuentes() {
        return frecuentes.listar();
    }

    public int tamanioFrecuentes() {
        return frecuentes.tamanio();
    }
}
