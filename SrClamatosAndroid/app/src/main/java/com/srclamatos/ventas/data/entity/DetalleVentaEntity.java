package com.srclamatos.ventas.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Normalización de los arrays embebidos en el struct Registro original:
 *   int idsBebidas[100]
 *   char nombresBebidas[100][60]
 *   char tamanosBebidas[100][10]
 *   int cantidadesBebidas[100]
 *   double preciosUnitariosBebidas[100]
 *   double subtotalesBebidas[100]
 *
 * Cada fila aquí = una posición de esos arrays.
 * Relación: una VentaEntity tiene muchos DetalleVentaEntity.
 */
@Entity(
    tableName = "detalles_venta",
    foreignKeys = @ForeignKey(
        entity = VentaEntity.class,
        parentColumns = "id",
        childColumns = "ventaId",
        onDelete = ForeignKey.CASCADE
    )
)
public class DetalleVentaEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(index = true)
    public int ventaId;

    public int bebidaId;

    @NonNull
    public String nombreBebida = "";

    /** "Chico", "Grande" o "Fijo" — equivalente a tamanosBebidas[i] */
    @NonNull
    public String tamano = "Fijo";

    public int cantidad;

    public double precioUnitario;

    public double subtotal;

    public DetalleVentaEntity() {}

    public DetalleVentaEntity(int ventaId, int bebidaId, @NonNull String nombreBebida,
                               @NonNull String tamano, int cantidad, double precioUnitario) {
        this.ventaId = ventaId;
        this.bebidaId = bebidaId;
        this.nombreBebida = nombreBebida;
        this.tamano = tamano;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    public String getResumen() {
        return String.format("%dx %s (%s) = $%.2f", cantidad, nombreBebida, tamano, subtotal);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVentaId() { return ventaId; }
    public void setVentaId(int ventaId) { this.ventaId = ventaId; }

    public int getBebidaId() { return bebidaId; }
    public void setBebidaId(int bebidaId) { this.bebidaId = bebidaId; }

    @NonNull
    public String getNombreBebida() { return nombreBebida; }
    public void setNombreBebida(@NonNull String nombreBebida) { this.nombreBebida = nombreBebida; }

    @NonNull
    public String getTamano() { return tamano; }
    public void setTamano(@NonNull String tamano) { this.tamano = tamano; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
