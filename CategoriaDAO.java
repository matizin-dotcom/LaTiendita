package com.discount3a.dao;

import java.util.List;

/**
 * Acceso a datos de categorías. Cada relación es {padre, hijo}.
 */
public interface CategoriaDAO {
    void guardar(List<String[]> relaciones);

    List<String[]> cargar();
}
