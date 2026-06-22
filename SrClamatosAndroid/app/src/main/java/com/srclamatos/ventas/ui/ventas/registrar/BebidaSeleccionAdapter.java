package com.srclamatos.ventas.ui.ventas.registrar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.BebidaEntity;

import java.util.ArrayList;
import java.util.List;

public class BebidaSeleccionAdapter extends RecyclerView.Adapter<BebidaSeleccionAdapter.ViewHolder> {

    public interface OnBebidaClickListener {
        void onClick(BebidaEntity bebida);
    }

    private final List<BebidaEntity> bebidas = new ArrayList<>();
    private final OnBebidaClickListener listener;

    public BebidaSeleccionAdapter(OnBebidaClickListener listener) {
        this.listener = listener;
    }

    public void setBebidas(List<BebidaEntity> lista) {
        bebidas.clear();
        if (lista != null) bebidas.addAll(lista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bebida_seleccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BebidaEntity bebida = bebidas.get(position);
        holder.textNombre.setText(bebida.nombre);
        holder.textPrecio.setText(bebida.getPrecioFormateado());
        holder.itemView.setOnClickListener(v -> listener.onClick(bebida));
    }

    @Override
    public int getItemCount() {
        return bebidas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre;
        TextView textPrecio;

        ViewHolder(View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.text_nombre);
            textPrecio = itemView.findViewById(R.id.text_precio);
        }
    }
}
