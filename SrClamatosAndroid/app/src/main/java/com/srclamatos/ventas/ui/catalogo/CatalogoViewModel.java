package com.srclamatos.ventas.ui.catalogo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.srclamatos.ventas.data.entity.BebidaEntity;
import com.srclamatos.ventas.data.repository.BebidaRepository;

import java.util.List;

public class CatalogoViewModel extends AndroidViewModel {

    private final BebidaRepository bebidaRepository;
    private final LiveData<List<BebidaEntity>> bebidasActivas;
    private final MutableLiveData<String> mensajeEvento = new MutableLiveData<>();

    public CatalogoViewModel(@NonNull Application application) {
        super(application);
        bebidaRepository = new BebidaRepository(application);
        bebidasActivas = bebidaRepository.getBebidasActivas();
    }

    public LiveData<List<BebidaEntity>> getBebidasActivas() {
        return bebidasActivas;
    }

    public LiveData<String> getMensajeEvento() {
        return mensajeEvento;
    }

    /** Equivalente a: opcion 3 en actualizarMenu() — Agregar bebida nueva */
    public void agregarBebida(String nombre, double precioChico, double precioGrande) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mensajeEvento.setValue("El nombre no puede estar vacío.");
            return;
        }
        if (precioChico <= 0) {
            mensajeEvento.setValue("El precio debe ser mayor que cero.");
            return;
        }
        BebidaEntity bebida = new BebidaEntity(nombre.trim(), precioChico, precioGrande);
        bebidaRepository.insertar(bebida, () ->
            mensajeEvento.postValue("Bebida \"" + nombre + "\" agregada al catálogo.")
        );
    }

    /** Equivalente a: opcion 1 en actualizarMenu() — Modificar bebida existente */
    public void actualizarBebida(BebidaEntity bebida) {
        if (bebida.nombre.trim().isEmpty()) {
            mensajeEvento.setValue("El nombre no puede estar vacío.");
            return;
        }
        bebidaRepository.actualizar(bebida, () ->
            mensajeEvento.postValue("Bebida actualizada correctamente.")
        );
    }

    /** Equivalente a: opcion 2 en actualizarMenu() — Eliminar bebida (soft delete) */
    public void eliminarBebida(int id, String nombre) {
        bebidaRepository.eliminar(id, () ->
            mensajeEvento.postValue("\"" + nombre + "\" eliminada del catálogo.")
        );
    }

    public void clearMensaje() {
        mensajeEvento.setValue(null);
    }
}
