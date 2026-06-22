package com.srclamatos.ventas.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.srclamatos.ventas.data.dao.BebidaDao;
import com.srclamatos.ventas.data.dao.DetalleVentaDao;
import com.srclamatos.ventas.data.dao.VentaDao;
import com.srclamatos.ventas.data.entity.BebidaEntity;
import com.srclamatos.ventas.data.entity.DetalleVentaEntity;
import com.srclamatos.ventas.data.entity.VentaEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reemplaza completamente el sistema de archivos binarios:
 *   registros.dat         → tablas 'bebidas' + 'ventas' + 'detalles_venta'
 *   ventas_historicas.dat → columna esHistorica = 1 en tabla 'ventas'
 *
 * El callback DatabaseCallback siembra el catálogo inicial de 15 bebidas,
 * equivalente a llenarCatalogoBase() + crearCatalogoDesdeCero().
 */
@Database(
    entities = {BebidaEntity.class, VentaEntity.class, DetalleVentaEntity.class},
    version = 1,
    exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "srclamatos.db";
    private static volatile AppDatabase INSTANCE;

    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    public abstract BebidaDao bebidaDao();
    public abstract VentaDao ventaDao();
    public abstract DetalleVentaDao detalleVentaDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .addCallback(new SeedCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Equivalent to: llenarCatalogoBase() + crearCatalogoDesdeCero()
     * Se ejecuta solo en la primera creación de la base de datos.
     */
    private static class SeedCallback extends RoomDatabase.Callback {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(() -> {
                BebidaDao dao = INSTANCE.bebidaDao();
                dao.insertarBebidas(buildCatalogoBase());
            });
        }
    }

    /**
     * Catálogo inicial — equivalente exacto de llenarCatalogoBase() en el código C.
     * precioGrande == 0 indica precio fijo (igual que la condición original).
     */
    private static List<BebidaEntity> buildCatalogoBase() {
        return Arrays.asList(
            new BebidaEntity("Original",        110, 130),
            new BebidaEntity("Pelao",           150, 170),
            new BebidaEntity("Almejon",         150, 170),
            new BebidaEntity("Sr. Clamatos",    170, 190),
            new BebidaEntity("Cuerazo",         110, 130),
            new BebidaEntity("Churroslocos",    100, 120),
            new BebidaEntity("Tostilocos",      110, 130),
            new BebidaEntity("Michelada",       120, 160),
            new BebidaEntity("Limonada",         65, 130),
            new BebidaEntity("Sangria",         130,   0),
            new BebidaEntity("Cheve",            60,   0),
            new BebidaEntity("Soda/Jugo",        30,   0),
            new BebidaEntity("Agua",             25,   0),
            new BebidaEntity("Extra Mariscos",   45,   0),
            new BebidaEntity("Carne seca",       70,   0)
        );
    }
}
