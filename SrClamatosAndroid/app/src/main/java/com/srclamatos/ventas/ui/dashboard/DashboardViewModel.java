package com.srclamatos.ventas.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.data.repository.VentaRepository;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final VentaRepository ventaRepository;
    private final LiveData<List<VentaConDetalles>> ventasDelDia;
    private final MutableLiveData<double[]> totalDia = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        ventaRepository = new VentaRepository(application);
        ventasDelDia = ventaRepository.getVentasDelDia();
        cargarTotalDia();
    }

    public LiveData<List<VentaConDetalles>> getVentasDelDia() {
        return ventasDelDia;
    }

    public LiveData<double[]> getTotalDia() {
        return totalDia;
    }

    public void cargarTotalDia() {
        ventaRepository.obtenerTotalDia(resultado -> totalDia.postValue(resultado));
    }

    public String getFechaHoy() {
        return VentaRepository.getFechaHoy();
    }
}
