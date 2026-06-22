package com.srclamatos.ventas.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.repository.VentaRepository;
import com.srclamatos.ventas.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_ventas,
                R.id.nav_catalogo,
                R.id.nav_reportes
        ).build();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // Archivar ventas pasadas al iniciar la app
        // Equivalente a: archivarVentasPasadas() en main()
        VentaRepository repo = new VentaRepository(getApplication());
        repo.archivarVentasPasadas(archivadas -> {
            // Se archivaron ventas de días anteriores silenciosamente
        });
    }
}
