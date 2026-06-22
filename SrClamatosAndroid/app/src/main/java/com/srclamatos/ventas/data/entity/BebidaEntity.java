package com.srclamatos.ventas.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Migrado desde: struct Registro con tipoRegistro == 1
 * Campos originales: id, nombre, precioChico, precioGrande, activa
 */
@Entity(tableName = "bebidas")
public class BebidaEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String nombre = "";

    public double precioChico;

    /**
     * Si precioGrande == 0, la bebida tiene precio fijo (solo precioChico).
     * Equivalente a la condición original: if (bebida->precioGrande > 0)
     */
    public double precioGrande;

    public int activa; // 1 = activa, 0 = eliminada (soft delete)

    public BebidaEntity() {}

    public BebidaEntity(@NonNull String nombre, double precioChico, double precioGrande) {
        this.nombre = nombre;
        this.precioChico = precioChico;
        this.precioGrande = precioGrande;
        this.activa = 1;
    }

    public boolean tieneTamanos() {
        return precioGrande > 0;
    }

    public String getPrecioFormateado() {
        if (tieneTamanos()) {
            return String.format("Ch: $%.2f | Gr: $%.2f", precioChico, precioGrande);
        }
        return String.format("$%.2f", precioChico);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getNombre() { return nombre; }
    public void setNombre(@NonNull String nombre) { this.nombre = nombre; }

    public double getPrecioChico() { return precioChico; }
    public void setPrecioChico(double precioChico) { this.precioChico = precioChico; }

    public double getPrecioGrande() { return precioGrande; }
    public void setPrecioGrande(double precioGrande) { this.precioGrande = precioGrande; }

    public int getActiva() { return activa; }
    public void setActiva(int activa) { this.activa = activa; }
}
