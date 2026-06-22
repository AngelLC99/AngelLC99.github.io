package com.srclamatos.ventas.ui.ventas.registrar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.DetalleVentaEntity;

import java.util.ArrayList;
import java.util.List;

public class DetalleVentaAdapter extends RecyclerView.Adapter<DetalleVentaAdapter.DetalleViewHolder> {

    private final List<DetalleVentaEntity> detalles = new ArrayList<>();

    public interface OnEliminarDetalleListener {
        void onEliminar(int position);
    }

    private final OnEliminarDetalleListener listener;

    public DetalleVentaAdapter(OnEliminarDetalleListener listener) {
        this.listener = listener;
    }

    public void setDetalles(List<DetalleVentaEntity> nuevosDetalles) {
        detalles.clear();
        if (nuevosDetalles != null) detalles.addAll(nuevosDetalles);
        notifyDataSetChanged();
    }

    public void agregarDetalle(DetalleVentaEntity detalle) {
        detalles.add(detalle);
        notifyItemInserted(detalles.size() - 1);
    }

    public List<DetalleVentaEntity> getDetalles() {
        return new ArrayList<>(detalles);
    }

    public double calcularTotal() {
        double total = 0;
        for (DetalleVentaEntity d : detalles) total += d.subtotal;
        return total;
    }

    @NonNull
    @Override
    public DetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detalle_venta, parent, false);
        return new DetalleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalleViewHolder holder, int position) {
        holder.bind(detalles.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    static class DetalleViewHolder extends RecyclerView.ViewHolder {
        private final TextView textNombre;
        private final TextView textTamano;
        private final TextView textCantidad;
        private final TextView textSubtotal;
        private final MaterialButton btnEliminar;

        DetalleViewHolder(View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.text_nombre_bebida);
            textTamano = itemView.findViewById(R.id.text_tamano);
            textCantidad = itemView.findViewById(R.id.text_cantidad);
            textSubtotal = itemView.findViewById(R.id.text_subtotal);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_detalle);
        }

        void bind(DetalleVentaEntity detalle, int position, OnEliminarDetalleListener listener) {
            textNombre.setText(detalle.nombreBebida);
            textTamano.setText(detalle.tamano);
            textCantidad.setText(detalle.cantidad + " x $" + String.format("%.2f", detalle.precioUnitario));
            textSubtotal.setText(String.format("$%.2f", detalle.subtotal));
            btnEliminar.setOnClickListener(v -> listener.onEliminar(position));
        }
    }
}
