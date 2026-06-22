package com.srclamatos.ventas.ui.ventas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.databinding.FragmentVentasBinding;
import com.srclamatos.ventas.ui.ventas.registrar.EditarVentaActivity;
import com.srclamatos.ventas.ui.ventas.registrar.RegistrarVentaActivity;

import java.util.List;

public class VentasFragment extends Fragment implements VentaAdapter.OnVentaListener {

    private FragmentVentasBinding binding;
    private VentasViewModel viewModel;
    private VentaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVentasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(VentasViewModel.class);

        setupRecyclerView();
        setupFab();
        setupBusqueda();
        setupOrdenar();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new VentaAdapter(this);
        binding.recyclerVentas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerVentas.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabNuevaVenta.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistrarVentaActivity.class);
            startActivity(intent);
        });
    }

    private void setupBusqueda() {
        binding.btnBuscar.setOnClickListener(v -> {
            String termino = binding.editBusqueda.getText() != null
                    ? binding.editBusqueda.getText().toString().trim()
                    : "";
            if (termino.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Escribe un nombre para buscar.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            mostrarResultadoBusqueda(termino);
        });

        binding.btnLimpiarBusqueda.setOnClickListener(v -> {
            binding.editBusqueda.setText("");
            binding.layoutResultadoBusqueda.setVisibility(View.GONE);
            binding.recyclerVentas.setVisibility(View.VISIBLE);
        });
    }

    private void mostrarResultadoBusqueda(String termino) {
        viewModel.buscarPorCliente(termino);
        viewModel.getResultadoBusqueda().observe(getViewLifecycleOwner(), ventas -> {
            if (ventas != null && !ventas.isEmpty()) {
                adapter.submitList(ventas);
                binding.recyclerVentas.setVisibility(View.VISIBLE);
                binding.textSinVentas.setVisibility(View.GONE);
            } else {
                binding.recyclerVentas.setVisibility(View.GONE);
                binding.textSinVentas.setVisibility(View.VISIBLE);
                binding.textSinVentas.setText("No se encontraron ventas para \"" + termino + "\".");
            }
        });
    }

    private void setupOrdenar() {
        binding.btnOrdenar.setOnClickListener(v -> {
            String[] opciones = {
                "Por ID (defecto)",
                "Por cliente (A-Z)",
                "Por fecha",
                "Por total (mayor a menor)"
            };
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Ordenar ventas")
                    .setItems(opciones, (dialog, which) -> {
                        viewModel.ordenarVentas(which + 1);
                        viewModel.getVentasOrdenadas().observe(getViewLifecycleOwner(), ventas -> {
                            if (ventas != null) {
                                adapter.submitList(ventas);
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void observeViewModel() {
        viewModel.getVentasActivas().observe(getViewLifecycleOwner(), ventas -> {
            if (ventas == null || ventas.isEmpty()) {
                binding.textSinVentas.setVisibility(View.VISIBLE);
                binding.recyclerVentas.setVisibility(View.GONE);
                binding.textSinVentas.setText(R.string.sin_ventas);
            } else {
                binding.textSinVentas.setVisibility(View.GONE);
                binding.recyclerVentas.setVisibility(View.VISIBLE);
                adapter.submitList(ventas);
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
    public void onEliminar(VentaConDetalles venta) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar venta")
                .setMessage(String.format(
                        "¿Eliminar la Venta #%d de %s por $%.2f?",
                        venta.venta.id, venta.venta.cliente, venta.venta.total))
                .setPositiveButton("Eliminar", (dialog, which) ->
                        viewModel.eliminarVenta(venta.venta.id))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onAgregarBebidas(VentaConDetalles venta) {
        Intent intent = new Intent(requireContext(), EditarVentaActivity.class);
        intent.putExtra(EditarVentaActivity.EXTRA_VENTA_ID, venta.venta.id);
        intent.putExtra(EditarVentaActivity.EXTRA_MODO, EditarVentaActivity.MODO_AGREGAR);
        startActivity(intent);
    }

    @Override
    public void onModificar(VentaConDetalles venta) {
        Intent intent = new Intent(requireContext(), EditarVentaActivity.class);
        intent.putExtra(EditarVentaActivity.EXTRA_VENTA_ID, venta.venta.id);
        intent.putExtra(EditarVentaActivity.EXTRA_MODO, EditarVentaActivity.MODO_MODIFICAR);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
