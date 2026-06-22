package com.srclamatos.ventas.ui.catalogo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.BebidaEntity;

public class BebidaAdapter extends ListAdapter<BebidaEntity, BebidaAdapter.BebidaViewHolder> {

    public interface OnBebidaListener {
        void onEditar(BebidaEntity bebida);
        void onEliminar(BebidaEntity bebida);
    }

    private final OnBebidaListener listener;

    public BebidaAdapter(OnBebidaListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<BebidaEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BebidaEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull BebidaEntity a, @NonNull BebidaEntity b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull BebidaEntity a, @NonNull BebidaEntity b) {
                    return a.nombre.equals(b.nombre)
                            && a.precioChico == b.precioChico
                            && a.precioGrande == b.precioGrande;
                }
            };

    @NonNull
    @Override
    public BebidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bebida_catalogo, parent, false);
        return new BebidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BebidaViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class BebidaViewHolder extends RecyclerView.ViewHolder {
        private final TextView textId;
        private final TextView textNombre;
        private final TextView textPrecio;
        private final MaterialButton btnEditar;
        private final MaterialButton btnEliminar;

        BebidaViewHolder(View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.text_id);
            textNombre = itemView.findViewById(R.id.text_nombre);
            textPrecio = itemView.findViewById(R.id.text_precio);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }

        void bind(BebidaEntity bebida, OnBebidaListener listener) {
            textId.setText("#" + bebida.id);
            textNombre.setText(bebida.nombre);
            textPrecio.setText(bebida.getPrecioFormateado());
            btnEditar.setOnClickListener(v -> listener.onEditar(bebida));
            btnEliminar.setOnClickListener(v -> listener.onEliminar(bebida));
        }
    }
}
