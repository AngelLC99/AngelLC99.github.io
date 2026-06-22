package com.srclamatos.ventas.ui.catalogo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.BebidaEntity;

/**
 * Diálogo reutilizable para agregar y editar bebidas.
 * Equivalente a: opcion 1 (modificar) y opcion 3 (agregar) de actualizarMenu() en C.
 */
public class BebidaDialogFragment extends DialogFragment {

    public interface OnGuardarBebidaListener {
        void onGuardar(String nombre, double precioChico, double precioGrande);
    }

    private static final String ARG_BEBIDA_ID = "bebida_id";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_PRECIO_CHICO = "precio_chico";
    private static final String ARG_PRECIO_GRANDE = "precio_grande";

    private OnGuardarBebidaListener listener;

    public static BebidaDialogFragment newInstanceAgregar() {
        return new BebidaDialogFragment();
    }

    public static BebidaDialogFragment newInstanceEditar(BebidaEntity bebida) {
        BebidaDialogFragment dialog = new BebidaDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BEBIDA_ID, bebida.id);
        args.putString(ARG_NOMBRE, bebida.nombre);
        args.putDouble(ARG_PRECIO_CHICO, bebida.precioChico);
        args.putDouble(ARG_PRECIO_GRANDE, bebida.precioGrande);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnGuardarListener(OnGuardarBebidaListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bebida, null);

        TextInputEditText editNombre = view.findViewById(R.id.edit_nombre);
        TextInputEditText editPrecioChico = view.findViewById(R.id.edit_precio_chico);
        TextInputEditText editPrecioGrande = view.findViewById(R.id.edit_precio_grande);
        RadioGroup radioTipoPrecio = view.findViewById(R.id.radio_tipo_precio);
        View layoutPrecioGrande = view.findViewById(R.id.layout_precio_grande);

        boolean esEdicion = getArguments() != null && getArguments().containsKey(ARG_NOMBRE);

        if (esEdicion) {
            Bundle args = getArguments();
            editNombre.setText(args.getString(ARG_NOMBRE));
            editPrecioChico.setText(String.format("%.2f", args.getDouble(ARG_PRECIO_CHICO)));
            double pg = args.getDouble(ARG_PRECIO_GRANDE);
            if (pg > 0) {
                radioTipoPrecio.check(R.id.radio_chico_grande);
                editPrecioGrande.setText(String.format("%.2f", pg));
                layoutPrecioGrande.setVisibility(View.VISIBLE);
            } else {
                radioTipoPrecio.check(R.id.radio_precio_fijo);
                layoutPrecioGrande.setVisibility(View.GONE);
            }
        } else {
            radioTipoPrecio.check(R.id.radio_precio_fijo);
            layoutPrecioGrande.setVisibility(View.GONE);
        }

        radioTipoPrecio.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_chico_grande) {
                layoutPrecioGrande.setVisibility(View.VISIBLE);
            } else {
                layoutPrecioGrande.setVisibility(View.GONE);
                editPrecioGrande.setText("");
            }
        });

        String titulo = esEdicion ? "Editar bebida" : "Agregar bebida";

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(titulo)
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editNombre.getText() != null
                            ? editNombre.getText().toString().trim() : "";
                    String strChico = editPrecioChico.getText() != null
                            ? editPrecioChico.getText().toString().trim() : "0";
                    String strGrande = editPrecioGrande.getText() != null
                            ? editPrecioGrande.getText().toString().trim() : "0";

                    try {
                        double chico = Double.parseDouble(strChico);
                        double grande = radioTipoPrecio.getCheckedRadioButtonId() == R.id.radio_chico_grande
                                ? Double.parseDouble(strGrande.isEmpty() ? "0" : strGrande)
                                : 0;
                        if (listener != null) listener.onGuardar(nombre, chico, grande);
                    } catch (NumberFormatException ignored) {}
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }
}
