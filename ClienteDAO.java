package com.discount3a.dao;

import com.discount3a.modelo.Cliente;
import java.util.List;

/**
 * Acceso a datos de clientes.
 */
public interface ClienteDAO {
    void guardar(List<Cliente> clientes);

    List<Cliente> cargar();
}
