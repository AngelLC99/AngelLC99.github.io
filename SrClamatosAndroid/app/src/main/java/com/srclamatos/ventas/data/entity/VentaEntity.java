package com.srclamatos.ventas.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Migrado desde: struct Registro con tipoRegistro == 2
 * Los arrays embebidos (idsBebidas[100], nombresBebidas[100][60], etc.)
 * se normalizaron en DetalleVentaEntity.
 */
@Entity(tableName = "ventas")
public class VentaEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String cliente = "Sin nombre";

    /** Formato DD-MM-YYYY, igual que el sistema original */
    @NonNull
    public String fecha = "";

    public double total;

    /** 1 = activa, 0 = eliminada (equivalente a activa en el struct original) */
    public int activa;

    /**
     * 0 = venta del día / activa en registros.dat
     * 1 = venta archivada (equivalente a ventas_historicas.dat)
     * Reemplaza el mecanismo de archivar en archivo separado
     */
    public int esHistorica;

    public VentaEntity() {}

    public VentaEntity(@NonNull String cliente, @NonNull String fecha, double total) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.activa = 1;
        this.esHistorica = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getCliente() { return cliente; }
    public void setCliente(@NonNull String cliente) { this.cliente = cliente; }

    @NonNull
    public String getFecha() { return fecha; }
    public void setFecha(@NonNull String fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getActiva() { return activa; }
    public void setActiva(int activa) { this.activa = activa; }

    public int getEsHistorica() { return esHistorica; }
    public void setEsHistorica(int esHistorica) { this.esHistorica = esHistorica; }
}
