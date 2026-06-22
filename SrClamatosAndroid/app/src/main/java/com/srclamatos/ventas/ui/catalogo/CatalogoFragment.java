package com.srclamatos.ventas.ui.catalogo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.srclamatos.ventas.databinding.FragmentCatalogoBinding;
import com.srclamatos.ventas.data.entity.BebidaEntity;

public class CatalogoFragment extends Fragment implements BebidaAdapter.OnBebidaListener {

    private FragmentCatalogoBinding binding;
    private CatalogoViewModel viewModel;
    private BebidaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CatalogoViewModel.class);

        setupRecyclerView();
        setupFab();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new BebidaAdapter(this);
        binding.recyclerBebidas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBebidas.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAgregarBebida.setOnClickListener(v -> {
            BebidaDialogFragment dialog = BebidaDialogFragment.newInstanceAgregar();
            dialog.setOnGuardarListener((nombre, chico, grande) ->
                    viewModel.agregarBebida(nombre, chico, grande));
            dialog.show(getChildFragmentManager(), "agregar_bebida");
        });
    }

    private void observeViewModel() {
        viewModel.getBebidasActivas().observe(getViewLifecycleOwner(), bebidas -> {
            if (bebidas == null || bebidas.isEmpty()) {
                binding.textSinBebidas.setVisibility(View.VISIBLE);
                binding.recyclerBebidas.setVisibility(View.GONE);
            } else {
                binding.textSinBebidas.setVisibility(View.GONE);
                binding.recyclerBebidas.setVisibility(View.VISIBLE);
                adapter.submitList(bebidas);
            }
        });

        viewModel.getMensajeEvento().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null) {
                Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_LONG).show();
                viewModel.clearMensaje();
            }
        });
    }

    @Override
    public void onEditar(BebidaEntity bebida) {
        BebidaDialogFragment dialog = BebidaDialogFragment.newInstanceEditar(bebida);
        dialog.setOnGuardarListener((nombre, chico, grande) -> {
            bebida.nombre = nombre;
            bebida.precioChico = chico;
            bebida.precioGrande = grande;
            viewModel.actualizarBebida(bebida);
        });
        dialog.show(getChildFragmentManager(), "editar_bebida");
    }

    @Override
    public void onEliminar(BebidaEntity bebida) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar bebida")
                .setMessage("¿Eliminar \"" + bebida.nombre + "\" del catálogo?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        viewModel.eliminarBebida(bebida.id, bebida.nombre))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
