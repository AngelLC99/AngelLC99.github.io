package com.srclamatos.ventas.ui.ventas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.data.repository.VentaRepository;

import java.util.List;

public class VentasViewModel extends AndroidViewModel {

    private final VentaRepository ventaRepository;
    private final LiveData<List<VentaConDetalles>> ventasActivas;
    private final MutableLiveData<String> mensajeEvento = new MutableLiveData<>();
    private final MutableLiveData<List<VentaConDetalles>> ventasOrdenadas = new MutableLiveData<>();
    private final MutableLiveData<List<VentaConDetalles>> resultadoBusqueda = new MutableLiveData<>();

    public VentasViewModel(@NonNull Application application) {
        super(application);
        ventaRepository = new VentaRepository(application);
        ventasActivas = ventaRepository.getVentasActivas();
    }

    public LiveData<List<VentaConDetalles>> getVentasActivas() {
        return ventasActivas;
    }

    public LiveData<String> getMensajeEvento() {
        return mensajeEvento;
    }

    public LiveData<List<VentaConDetalles>> getVentasOrdenadas() {
        return ventasOrdenadas;
    }

    public LiveData<List<VentaConDetalles>> getResultadoBusqueda() {
        return resultadoBusqueda;
    }

    /** Equivalente a: venta.activa = 0; reescribirRegistro(...) */
    public void eliminarVenta(int id) {
        ventaRepository.eliminarVenta(id, () ->
            mensajeEvento.postValue("Venta eliminada correctamente.")
        );
    }

    /**
     * Equivalente a: consultarVentasOrdenadas()
     * criterio: 1=ID, 2=Cliente, 3=Fecha, 4=Total
     */
    public void ordenarVentas(int criterio) {
        ventaRepository.obtenerVentasOrdenadas(criterio, ventas ->
            ventasOrdenadas.postValue(ventas)
        );
    }

    /** Equivalente a: buscarVentasPorCliente() */
    public void buscarPorCliente(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            mensajeEvento.postValue("Escribe al menos una letra del nombre.");
            return;
        }
        ventaRepository.buscarPorCliente(termino.trim(), ventas -> {
            resultadoBusqueda.postValue(ventas);
            if (ventas == null || ventas.isEmpty()) {
                mensajeEvento.postValue("No se encontraron ventas para \"" + termino + "\".");
            }
        });
    }

    public void clearMensaje() {
        mensajeEvento.setValue(null);
    }
}
