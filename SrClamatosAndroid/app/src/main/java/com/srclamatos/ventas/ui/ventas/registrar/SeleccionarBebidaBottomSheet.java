package com.srclamatos.ventas.ui.ventas.registrar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.BebidaEntity;
import com.srclamatos.ventas.databinding.BottomSheetSeleccionarBebidaBinding;

import java.util.List;

/**
 * Equivalente a: seleccionarBebida() en C.
 * Muestra el catálogo de bebidas para que el usuario elija una.
 */
public class SeleccionarBebidaBottomSheet extends BottomSheetDialogFragment {

    public interface OnBebidaSeleccionadaListener {
        void onBebidaSeleccionada(BebidaEntity bebida);
    }

    private BottomSheetSeleccionarBebidaBinding binding;
    private List<BebidaEntity> bebidas;
    private OnBebidaSeleccionadaListener listener;

    public static SeleccionarBebidaBottomSheet newInstance(List<BebidaEntity> bebidas,
                                                            OnBebidaSeleccionadaListener listener) {
        SeleccionarBebidaBottomSheet sheet = new SeleccionarBebidaBottomSheet();
        sheet.bebidas = bebidas;
        sheet.listener = listener;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSeleccionarBebidaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BebidaSeleccionAdapter adapter = new BebidaSeleccionAdapter(bebida -> {
            if (listener != null) listener.onBebidaSeleccionada(bebida);
            dismiss();
        });
        adapter.setBebidas(bebidas);

        binding.recyclerBebidas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBebidas.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
