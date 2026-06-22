package com.srclamatos.ventas.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.srclamatos.ventas.R;
import com.srclamatos.ventas.databinding.FragmentDashboardBinding;
import com.srclamatos.ventas.ui.ventas.registrar.RegistrarVentaActivity;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding.textFechaHoy.setText("Hoy: " + viewModel.getFechaHoy());

        viewModel.getVentasDelDia().observe(getViewLifecycleOwner(), ventas -> {
            int cantidad = ventas != null ? ventas.size() : 0;
            binding.textCantidadVentas.setText(String.valueOf(cantidad));
        });

        viewModel.getTotalDia().observe(getViewLifecycleOwner(), datos -> {
            if (datos != null) {
                binding.textTotalDia.setText(String.format("$%.2f", datos[0]));
                binding.textCantidadVentas.setText(String.valueOf((int) datos[1]));
            }
        });

        binding.btnNuevaVenta.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistrarVentaActivity.class);
            startActivity(intent);
        });

        binding.btnVerVentas.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.nav_ventas)
        );

        binding.btnVerCatalogo.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.nav_catalogo)
        );

        binding.btnVerReportes.setOnClickListener(v ->
            Navigation.findNavController(view).navigate(R.id.nav_reportes)
        );
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
