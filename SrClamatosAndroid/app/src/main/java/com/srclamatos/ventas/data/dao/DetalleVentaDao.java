package com.srclamatos.ventas.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.srclamatos.ventas.data.entity.DetalleVentaEntity;

import java.util.List;

/**
 * Reemplaza el manejo de los arrays embebidos en struct Registro:
 *   idsBebidas[], nombresBebidas[][], tamanosBebidas[][], cantidadesBebidas[],
 *   preciosUnitariosBebidas[], subtotalesBebidas[]
 */
@Dao
public interface DetalleVentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarDetalle(DetalleVentaEntity detalle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarDetalles(List<DetalleVentaEntity> detalles);

    /** Elimina todos los detalles de una venta (para re-capturar la venta completa) */
    @Query("DELETE FROM detalles_venta WHERE ventaId = :ventaId")
    void eliminarDetallesPorVenta(int ventaId);

    @Query("SELECT * FROM detalles_venta WHERE ventaId = :ventaId ORDER BY id ASC")
    List<DetalleVentaEntity> obtenerDetallesPorVenta(int ventaId);

    @Query("SELECT SUM(subtotal) FROM detalles_venta WHERE ventaId = :ventaId")
    double calcularTotalPorVenta(int ventaId);
}
