package com.srclamatos.ventas.ui.reportes;

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
import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.databinding.FragmentReportesBinding;
import com.srclamatos.ventas.ui.ventas.VentaAdapter;

import java.util.List;

public class ReportesFragment extends Fragment {

    private FragmentReportesBinding binding;
    private ReportesViewModel viewModel;
    private VentaAdapter adaptadorResultados;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReportesViewModel.class);

        setupRecyclerView();
        setupBotones();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adaptadorResultados = new VentaAdapter(new VentaAdapter.OnVentaListener() {
            @Override public void onEliminar(VentaConDetalles venta) {}
            @Override public void onAgregarBebidas(VentaConDetalles venta) {}
            @Override public void onModificar(VentaConDetalles venta) {}
        });
        binding.recyclerResultados.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerResultados.setAdapter(adaptadorResultados);
    }

    private void setupBotones() {
        binding.btnTotalDia.setOnClickListener(v -> {
            viewModel.cargarTotalDia();
            binding.layoutTotalDia.setVisibility(View.VISIBLE);
            binding.recyclerResultados.setVisibility(View.GONE);
        });

        binding.btnBuscarCliente.setOnClickListener(v -> {
            String termino = binding.editBusquedaCliente.getText() != null
                    ? binding.editBusquedaCliente.getText().toString().trim() : "";
            viewModel.buscarPorCliente(termino);
        });

        binding.btnOrdenarVentas.setOnClickListener(v -> {
            String[] opciones = {
                "Por ID (defecto)",
                "Por cliente (A-Z)",
                "Por fecha (reciente primero)",
                "Por total (mayor a menor)"
            };
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Ordenar ventas")
                    .setItems(opciones, (dialog, which) -> {
                        viewModel.ordenarVentas(which + 1);
                        binding.layoutTotalDia.setVisibility(View.GONE);
                        binding.recyclerResultados.setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.btnArchivarPasadas.setOnClickListener(v ->
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Archivar ventas pasadas")
                    .setMessage("Esto moverá todas las ventas de días anteriores al historial. ¿Continuar?")
                    .setPositiveButton("Archivar", (dialog, which) -> viewModel.archivarVentasPasadas())
                    .setNegativeButton("Cancelar", null)
                    .show()
        );

        binding.btnVerHistorico.setOnClickListener(v -> {
            binding.layoutTotalDia.setVisibility(View.GONE);
            binding.recyclerResultados.setVisibility(View.VISIBLE);
            viewModel.getVentasHistoricas().observe(getViewLifecycleOwner(), ventas -> {
                if (ventas != null) adaptadorResultados.submitList(ventas);
                actualizarEstadoLista(ventas);
            });
        });
    }

    private void observeViewModel() {
        viewModel.getTotalDia().observe(getViewLifecycleOwner(), datos -> {
            if (datos != null) {
                binding.textFechaDia.setText("Fecha: " + viewModel.getFechaHoy());
                binding.textCantidadVentasDia.setText("Ventas: " + (int) datos[1]);
                binding.textTotalAcumulado.setText(String.format("Total: $%.2f", datos[0]));
            }
        });

        viewModel.getResultadoBusqueda().observe(getViewLifecycleOwner(), ventas -> {
            binding.layoutTotalDia.setVisibility(View.GONE);
            binding.recyclerResultados.setVisibility(View.VISIBLE);
            adaptadorResultados.submitList(ventas);
            actualizarEstadoLista(ventas);
        });

        viewModel.getVentasOrdenadas().observe(getViewLifecycleOwner(), ventas -> {
            adaptadorResultados.submitList(ventas);
            actualizarEstadoLista(ventas);
        });

        viewModel.getMensajeEvento().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null) {
                Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_LONG).show();
                viewModel.clearMensaje();
            }
        });

        viewModel.getVentasArchivadas().observe(getViewLifecycleOwner(), archivadas -> {
            if (archivadas != null) {
                String msg = archivadas > 0
                        ? archivadas + " ventas archivadas correctamente."
                        : "No había ventas pasadas por archivar.";
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarEstadoLista(@Nullable List<VentaConDetalles> ventas) {
        boolean vacia = ventas == null || ventas.isEmpty();
        binding.textSinResultados.setVisibility(vacia ? View.VISIBLE : View.GONE);
        binding.recyclerResultados.setVisibility(vacia ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.cargarTotalDia();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
