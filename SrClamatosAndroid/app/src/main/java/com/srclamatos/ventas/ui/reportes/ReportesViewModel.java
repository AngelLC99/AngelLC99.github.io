package com.srclamatos.ventas.ui.reportes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.data.repository.VentaRepository;

import java.util.List;

public class ReportesViewModel extends AndroidViewModel {

    private final VentaRepository ventaRepository;
    private final LiveData<List<VentaConDetalles>> ventasHistoricas;
    private final MutableLiveData<double[]> totalDia = new MutableLiveData<>();
    private final MutableLiveData<List<VentaConDetalles>> resultadoBusqueda = new MutableLiveData<>();
    private final MutableLiveData<List<VentaConDetalles>> ventasOrdenadas = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeEvento = new MutableLiveData<>();
    private final MutableLiveData<Integer> ventasArchivadas = new MutableLiveData<>();

    public ReportesViewModel(@NonNull Application application) {
        super(application);
        ventaRepository = new VentaRepository(application);
        ventasHistoricas = ventaRepository.getVentasHistoricas();
        cargarTotalDia();
    }

    public LiveData<List<VentaConDetalles>> getVentasHistoricas() {
        return ventasHistoricas;
    }

    public LiveData<double[]> getTotalDia() {
        return totalDia;
    }

    public LiveData<List<VentaConDetalles>> getResultadoBusqueda() {
        return resultadoBusqueda;
    }

    public LiveData<List<VentaConDetalles>> getVentasOrdenadas() {
        return ventasOrdenadas;
    }

    public LiveData<String> getMensajeEvento() {
        return mensajeEvento;
    }

    public LiveData<Integer> getVentasArchivadas() {
        return ventasArchivadas;
    }

    /** Equivalente a: mostrarTotalVentasDia() */
    public void cargarTotalDia() {
        ventaRepository.obtenerTotalDia(resultado -> totalDia.postValue(resultado));
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

    /** Equivalente a: consultarVentasOrdenadas() con criterio numérico */
    public void ordenarVentas(int criterio) {
        ventaRepository.obtenerVentasOrdenadas(criterio, ventas ->
            ventasOrdenadas.postValue(ventas)
        );
    }

    /** Equivalente a: archivarVentasPasadas() desde opción 9 del menú */
    public void archivarVentasPasadas() {
        ventaRepository.archivarVentasPasadas(archivadas ->
            ventasArchivadas.postValue(archivadas)
        );
    }

    public String getFechaHoy() {
        return VentaRepository.getFechaHoy();
    }

    public void clearMensaje() {
        mensajeEvento.setValue(null);
    }
}
