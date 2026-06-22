package com.srclamatos.ventas.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.srclamatos.ventas.data.entity.BebidaEntity;

import java.util.List;

/**
 * Reemplaza las funciones de manejo de archivos para bebidas:
 *   buscarRegistroPorTipoId(1, ...)
 *   mostrarCatalogo()
 *   obtenerMaxIdBebida()
 *   actualizarMenu() -> sub-funciones agregar/modificar/eliminar
 */
@Dao
public interface BebidaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarBebida(BebidaEntity bebida);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertarBebidas(List<BebidaEntity> bebidas);

    @Update
    void actualizarBebida(BebidaEntity bebida);

    /** Equivalente a: bebida.activa = 0; reescribirRegistro(posicion, &bebida) */
    @Query("UPDATE bebidas SET activa = 0 WHERE id = :id")
    void eliminarBebida(int id);

    /** Equivalente a: mostrarCatalogo() — lee tipoRegistro==1 && activa */
    @Query("SELECT * FROM bebidas WHERE activa = 1 ORDER BY id ASC")
    LiveData<List<BebidaEntity>> obtenerBebidasActivas();

    @Query("SELECT * FROM bebidas WHERE activa = 1 ORDER BY id ASC")
    List<BebidaEntity> obtenerBebidasActivasSync();

    /** Equivalente a: buscarRegistroPorTipoId(1, id, ...) */
    @Query("SELECT * FROM bebidas WHERE id = :id AND activa = 1 LIMIT 1")
    BebidaEntity buscarBebidaPorId(int id);

    /** Equivalente a: obtenerMaxIdBebida() — Room auto-increment maneja IDs, pero se expone para UI */
    @Query("SELECT MAX(id) FROM bebidas")
    int obtenerMaxId();

    @Query("SELECT COUNT(*) FROM bebidas WHERE activa = 1")
    int contarBebidasActivas();
}
