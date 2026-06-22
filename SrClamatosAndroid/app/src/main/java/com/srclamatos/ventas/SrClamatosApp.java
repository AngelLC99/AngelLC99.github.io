package com.srclamatos.ventas;

import android.app.Application;

import com.srclamatos.ventas.data.database.AppDatabase;

public class SrClamatosApp extends Application {

    private static SrClamatosApp instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = AppDatabase.getInstance(this);
    }

    public static SrClamatosApp getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
