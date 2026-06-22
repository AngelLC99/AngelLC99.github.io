package com.srclamatos.ventas.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.srclamatos.ventas.data.dao.BebidaDao;
import com.srclamatos.ventas.data.database.AppDatabase;
import com.srclamatos.ventas.data.entity.BebidaEntity;

import java.util.List;

public class BebidaRepository {

    private final BebidaDao bebidaDao;
    private final LiveData<List<BebidaEntity>> bebidasActivas;

    public BebidaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        bebidaDao = db.bebidaDao();
        bebidasActivas = bebidaDao.obtenerBebidasActivas();
    }

    public LiveData<List<BebidaEntity>> getBebidasActivas() {
        return bebidasActivas;
    }

    public void insertar(BebidaEntity bebida, Runnable onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            bebidaDao.insertarBebida(bebida);
            if (onComplete != null) onComplete.run();
        });
    }

    public void actualizar(BebidaEntity bebida, Runnable onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            bebidaDao.actualizarBebida(bebida);
            if (onComplete != null) onComplete.run();
        });
    }

    public void eliminar(int id, Runnable onComplete) {
        AppDatabase.databaseExecutor.execute(() -> {
            bebidaDao.eliminarBebida(id);
            if (onComplete != null) onComplete.run();
        });
    }

    public void buscarPorId(int id, Callback<BebidaEntity> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            BebidaEntity bebida = bebidaDao.buscarBebidaPorId(id);
            if (callback != null) callback.onResult(bebida);
        });
    }

    public void obtenerBebidasSync(Callback<List<BebidaEntity>> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            List<BebidaEntity> bebidas = bebidaDao.obtenerBebidasActivasSync();
            if (callback != null) callback.onResult(bebidas);
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
