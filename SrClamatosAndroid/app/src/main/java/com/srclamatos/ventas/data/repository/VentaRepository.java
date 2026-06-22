package com.srclamatos.ventas.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.srclamatos.ventas.data.dao.DetalleVentaDao;
import com.srclamatos.ventas.data.dao.VentaDao;
import com.srclamatos.ventas.data.database.AppDatabase;
import com.srclamatos.ventas.data.entity.DetalleVentaEntity;
import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.data.entity.VentaEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VentaRepository {

    private final VentaDao ventaDao;
    private final DetalleVentaDao detalleVentaDao;
    private final LiveData<List<VentaConDetalles>> ventasActivas;

    public VentaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        ventaDao = db.ventaDao();
        detalleVentaDao = db.detalleVentaDao();
        ventasActivas = ventaDao.obtenerVentasActivas();
    }

    public LiveData<List<VentaConDetalles>> getVentasActivas() {
        return ventasActivas;
    }

    public LiveData<List<VentaConDetalles>> getVentasDelDia() {
        return ventaDao.obtenerVentasDelDia(getFechaHoy());
    }

    public LiveData<List<VentaConDetalles>> getVentasHistoricas() {
        return ventaDao.obtenerVentasHistoricas();
    }

    /**
     * Guarda una venta nueva con todos sus detalles en una transacción.
     * Equivalente a: escribirRegistro(&venta) en el código C,
     * pero normalizado: inserta VentaEntity + N DetalleVentaEntity.
     */
    public void insertarVentaConDetalles(VentaEntity venta,
                                          List<DetalleVentaEntity> detalles,
                                          Callback<Long> onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            long ventaId = ventaDao.insertarVenta(venta);
            for (DetalleVentaEntity detalle : detalles) {
                detalle.ventaId = (int) ventaId;
            }
            detalleVentaDao.insertarDetalles(detalles);
            if (onComplete != null) onComplete.onResult(ventaId);
        });
    }

    /**
     * Reemplaza completamente los detalles de una venta.
     * Equivalente a: reescribirRegistro(posicion, &venta) en el código C.
     * Usado por: agregarBebidasAVentaExistente() y modificarVenta().
     */
    public void actualizarVentaConDetalles(VentaEntity venta,
                                            List<DetalleVentaEntity> detalles,
                                            Runnable onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            ventaDao.actualizarVenta(venta);
            detalleVentaDao.eliminarDetallesPorVenta(venta.id);
            for (DetalleVentaEntity detalle : detalles) {
                detalle.ventaId = venta.id;
            }
            detalleVentaDao.insertarDetalles(detalles);
            if (onComplete != null) onComplete.run();
        });
    }

    /**
     * Agrega detalles adicionales sin reemplazar los existentes.
     * Equivalente a: agregarBebidasAVentaExistente() en el código C.
     */
    public void agregarDetallesAVenta(VentaEntity venta,
                                       List<DetalleVentaEntity> nuevosDetalles,
                                       Runnable onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            ventaDao.actualizarVenta(venta);
            for (DetalleVentaEntity detalle : nuevosDetalles) {
                detalle.ventaId = venta.id;
            }
            detalleVentaDao.insertarDetalles(nuevosDetalles);
            if (onComplete != null) onComplete.run();
        });
    }

    /** Equivalente a: venta.activa = 0; reescribirRegistro(...) */
    public void eliminarVenta(int id, Runnable onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            ventaDao.eliminarVenta(id);
            if (onComplete != null) onComplete.run();
        });
    }

    public void buscarPorId(int id, Callback<VentaConDetalles> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            VentaConDetalles venta = ventaDao.buscarVentaPorId(id);
            if (callback != null) callback.onResult(venta);
        });
    }

    /**
     * Equivalente a: archivarVentasPasadas()
     * Mueve ventas de días anteriores a estado histórico.
     */
    public void archivarVentasPasadas(Callback<Integer> onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            int archivadas = ventaDao.archivarVentasPasadas(getFechaHoy());
            if (onComplete != null) onComplete.onResult(archivadas);
        });
    }

    public void obtenerTotalDia(Callback<double[]> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            String hoy = getFechaHoy();
            double total = ventaDao.obtenerTotalDia(hoy);
            int cantidad = ventaDao.obtenerCantidadVentasDia(hoy);
            if (callback != null) callback.onResult(new double[]{total, cantidad});
        });
    }

    /**
     * Equivalente a: buscarVentasPorCliente()
     * Busca en ventas activas e históricas.
     */
    public void buscarPorCliente(String termino, Callback<List<VentaConDetalles>> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            List<VentaConDetalles> resultado = ventaDao.buscarPorCliente(termino);
            if (callback != null) callback.onResult(resultado);
        });
    }

    public void obtenerVentasOrdenadas(int criterio, Callback<List<VentaConDetalles>> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            List<VentaConDetalles> ventas;
            switch (criterio) {
                case 2: ventas = ventaDao.obtenerVentasOrdenadaPorCliente(); break;
                case 3: ventas = ventaDao.obtenerVentasOrdenadaPorFecha(); break;
                case 4: ventas = ventaDao.obtenerVentasOrdenadaPorTotal(); break;
                default: ventas = ventaDao.obtenerVentasActivasSync(); break;
            }
            if (callback != null) callback.onResult(ventas);
        });
    }

    /** Equivalente a: obtenerFechaActual() — formato DD-MM-YYYY */
    public static String getFechaHoy() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
