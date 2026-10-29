package com.discount3a.dao;

import com.discount3a.structures.MatrizEstantes;

/**
 * Acceso a datos de la distribución de estantes.
 */
public interface UbicacionDAO {
    void guardar(MatrizEstantes matriz);

    void cargar(MatrizEstantes matriz);
}
