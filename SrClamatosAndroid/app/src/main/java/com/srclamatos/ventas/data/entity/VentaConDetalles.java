package com.srclamatos.ventas.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de relación Room que agrupa una venta con todos sus detalles.
 * Equivalente a la estructura en C donde la venta contenía
 * los arrays de bebidas inline dentro del mismo Registro.
 */
public class VentaConDetalles {

    @Embedded
    public VentaEntity venta;

    @Relation(
        parentColumn = "id",
        entityColumn = "ventaId"
    )
    public List<DetalleVentaEntity> detalles;

    public VentaConDetalles() {
        this.detalles = new ArrayList<>();
    }

    public double calcularTotal() {
        double total = 0;
        for (DetalleVentaEntity detalle : detalles) {
            total += detalle.subtotal;
        }
        return total;
    }

    public int getNumeroBebidas() {
        return detalles != null ? detalles.size() : 0;
    }

    public String getResumenBebidas() {
        if (detalles == null || detalles.isEmpty()) {
            return "Sin bebidas";
        }
        if (detalles.size() == 1) {
            return detalles.get(0).nombreBebida;
        }
        return detalles.get(0).nombreBebida + " y " + (detalles.size() - 1) + " más";
    }
}
