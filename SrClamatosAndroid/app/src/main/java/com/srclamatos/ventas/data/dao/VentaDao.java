package com.srclamatos.ventas.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.data.entity.VentaEntity;

import java.util.List;

/**
 * Reemplaza las funciones de acceso a ventas:
 *   cargarVentas()
 *   buscarRegistroPorTipoId(2, ...)
 *   obtenerSiguienteIdVenta()
 *   archivarVentasPasadas()
 *   mostrarTotalVentasDia()
 *   buscarVentasPorCliente()
 */
@Dao
public interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarVenta(VentaEntity venta);

    @Update
    void actualizarVenta(VentaEntity venta);

    /** Soft delete — equivalente a: venta.activa = 0; reescribirRegistro(...) */
    @Query("UPDATE ventas SET activa = 0 WHERE id = :id")
    void eliminarVenta(int id);

    /**
     * Equivalente a: cargarVentas() — lee tipoRegistro==2 && activa && !esHistorica
     * LiveData notifica cambios automáticamente (reemplaza el bucle fread continuo)
     */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 0 ORDER BY id DESC")
    LiveData<List<VentaConDetalles>> obtenerVentasActivas();

    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 0 ORDER BY id DESC")
    List<VentaConDetalles> obtenerVentasActivasSync();

    /** Equivalente a: buscarRegistroPorTipoId(2, id, ...) */
    @Transaction
    @Query("SELECT * FROM ventas WHERE id = :id AND activa = 1 LIMIT 1")
    VentaConDetalles buscarVentaPorId(int id);

    /**
     * Equivalente a: mostrarTotalVentasDia()
     * Suma total de ventas activas de la fecha indicada
     */
    @Query("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE activa = 1 AND esHistorica = 0 AND fecha = :fecha")
    double obtenerTotalDia(String fecha);

    @Query("SELECT COUNT(*) FROM ventas WHERE activa = 1 AND esHistorica = 0 AND fecha = :fecha")
    int obtenerCantidadVentasDia(String fecha);

    /**
     * Equivalente a: archivarVentasPasadas()
     * Marca como históricas las ventas de fechas anteriores a hoy
     */
    @Query("UPDATE ventas SET esHistorica = 1 WHERE activa = 1 AND esHistorica = 0 AND fecha != :fechaHoy")
    int archivarVentasPasadas(String fechaHoy);

    /**
     * Equivalente a: buscarVentasPorCliente()
     * Busca en ventas activas e históricas
     */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND cliente LIKE '%' || :termino || '%' ORDER BY fecha DESC, id DESC")
    List<VentaConDetalles> buscarPorCliente(String termino);

    /** Todas las ventas activas del día para el dashboard */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 0 AND fecha = :fecha ORDER BY id DESC")
    LiveData<List<VentaConDetalles>> obtenerVentasDelDia(String fecha);

    /** Ventas ordenadas por cliente */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 0 ORDER BY cliente ASC")
    List<VentaConDetalles> obtenerVentasOrdenadaPorCliente();

    /** Ventas ordenadas por total descendente */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 0 ORDER BY total DESC")
    List<VentaConDetalles> obtenerVentasOrdenadaPorTotal();

    /** Ventas ordenadas por fecha descendente */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 0 ORDER BY fecha DESC, id DESC")
    List<VentaConDetalles> obtenerVentasOrdenadaPorFecha();

    /** Historial completo de ventas archivadas */
    @Transaction
    @Query("SELECT * FROM ventas WHERE activa = 1 AND esHistorica = 1 ORDER BY fecha DESC, id DESC")
    LiveData<List<VentaConDetalles>> obtenerVentasHistoricas();
}
