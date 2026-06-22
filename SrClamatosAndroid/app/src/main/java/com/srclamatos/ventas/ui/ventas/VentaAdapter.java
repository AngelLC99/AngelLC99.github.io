package com.srclamatos.ventas.ui.ventas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.DetalleVentaEntity;
import com.srclamatos.ventas.data.entity.VentaConDetalles;

public class VentaAdapter extends ListAdapter<VentaConDetalles, VentaAdapter.VentaViewHolder> {

    public interface OnVentaListener {
        void onEliminar(VentaConDetalles venta);
        void onAgregarBebidas(VentaConDetalles venta);
        void onModificar(VentaConDetalles venta);
    }

    private final OnVentaListener listener;

    public VentaAdapter(OnVentaListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<VentaConDetalles> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<VentaConDetalles>() {
                @Override
                public boolean areItemsTheSame(@NonNull VentaConDetalles a, @NonNull VentaConDetalles b) {
                    return a.venta.id == b.venta.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull VentaConDetalles a, @NonNull VentaConDetalles b) {
                    return a.venta.total == b.venta.total
                            && a.venta.cliente.equals(b.venta.cliente)
                            && a.detalles.size() == b.detalles.size();
                }
            };

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_venta, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class VentaViewHolder extends RecyclerView.ViewHolder {

        private final TextView textVentaId;
        private final TextView textCliente;
        private final TextView textFecha;
        private final TextView textTotal;
        private final TextView textBebidas;
        private final MaterialButton btnEliminar;
        private final MaterialButton btnAgregarBebidas;
        private final MaterialButton btnModificar;

        VentaViewHolder(View itemView) {
            super(itemView);
            textVentaId = itemView.findViewById(R.id.text_venta_id);
            textCliente = itemView.findViewById(R.id.text_cliente);
            textFecha = itemView.findViewById(R.id.text_fecha);
            textTotal = itemView.findViewById(R.id.text_total);
            textBebidas = itemView.findViewById(R.id.text_bebidas);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
            btnAgregarBebidas = itemView.findViewById(R.id.btn_agregar_bebidas);
            btnModificar = itemView.findViewById(R.id.btn_modificar);
        }

        void bind(VentaConDetalles ventaConDetalles, OnVentaListener listener) {
            textVentaId.setText("Venta #" + ventaConDetalles.venta.id);
            textCliente.setText(ventaConDetalles.venta.cliente);
            textFecha.setText(ventaConDetalles.venta.fecha);
            textTotal.setText(String.format("$%.2f", ventaConDetalles.venta.total));

            StringBuilder bebidasStr = new StringBuilder();
            if (ventaConDetalles.detalles != null) {
                for (int i = 0; i < ventaConDetalles.detalles.size(); i++) {
                    DetalleVentaEntity d = ventaConDetalles.detalles.get(i);
                    bebidasStr.append(String.format("• %dx %s (%s) = $%.2f",
                            d.cantidad, d.nombreBebida, d.tamano, d.subtotal));
                    if (i < ventaConDetalles.detalles.size() - 1) {
                        bebidasStr.append("\n");
                    }
                }
            }
            textBebidas.setText(bebidasStr.toString());

            btnEliminar.setOnClickListener(v -> listener.onEliminar(ventaConDetalles));
            btnAgregarBebidas.setOnClickListener(v -> listener.onAgregarBebidas(ventaConDetalles));
            btnModificar.setOnClickListener(v -> listener.onModificar(ventaConDetalles));
        }
    }
}
