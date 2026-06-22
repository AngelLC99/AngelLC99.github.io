package com.srclamatos.ventas.ui.ventas.registrar;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.srclamatos.ventas.R;
import com.srclamatos.ventas.data.entity.BebidaEntity;
import com.srclamatos.ventas.data.entity.DetalleVentaEntity;
import com.srclamatos.ventas.data.entity.VentaEntity;
import com.srclamatos.ventas.data.repository.BebidaRepository;
import com.srclamatos.ventas.data.repository.VentaRepository;
import com.srclamatos.ventas.databinding.ActivityRegistrarVentaBinding;

import java.util.List;

/**
 * Equivalente a: registrarVenta() + capturarDatosVenta() + agregarBebidaAVenta()
 * en el código C original.
 *
 * Flujo:
 * 1. Usuario agrega bebidas (BottomSheet selección → diálogo tamaño → cantidad)
 * 2. Ingresa nombre de cliente (opcional)
 * 3. Se guarda la venta con todos sus detalles
 */
public class RegistrarVentaActivity extends AppCompatActivity {

    private ActivityRegistrarVentaBinding binding;
    private DetalleVentaAdapter detalleAdapter;
    private BebidaRepository bebidaRepository;
    private VentaRepository ventaRepository;
    private List<BebidaEntity> catalogoBebidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrarVentaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bebidaRepository = new BebidaRepository(getApplication());
        ventaRepository = new VentaRepository(getApplication());

        setupRecyclerView();
        setupBotones();
        cargarCatalogo();
    }

    private void setupRecyclerView() {
        detalleAdapter = new DetalleVentaAdapter(position -> {
            List<DetalleVentaEntity> detalles = detalleAdapter.getDetalles();
            detalles.remove(position);
            detalleAdapter.setDetalles(detalles);
            actualizarTotal();
        });
        binding.recyclerDetalles.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerDetalles.setAdapter(detalleAdapter);
    }

    private void setupBotones() {
        binding.btnAgregarBebida.setOnClickListener(v -> mostrarSeleccionBebida());
        binding.btnGuardarVenta.setOnClickListener(v -> guardarVenta());
    }

    private void cargarCatalogo() {
        bebidaRepository.obtenerBebidasSync(bebidas -> {
            catalogoBebidas = bebidas;
        });
        bebidaRepository.getBebidasActivas().observe(this, bebidas -> {
            catalogoBebidas = bebidas;
        });
    }

    /**
     * Equivalente a: seleccionarBebida() → seleccionarTamanoPrecio() → leer cantidad
     */
    private void mostrarSeleccionBebida() {
        if (catalogoBebidas == null || catalogoBebidas.isEmpty()) {
            Snackbar.make(binding.getRoot(), "No hay bebidas en el catálogo.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        SeleccionarBebidaBottomSheet sheet = SeleccionarBebidaBottomSheet.newInstance(
                catalogoBebidas,
                bebida -> mostrarSeleccionTamano(bebida)
        );
        sheet.show(getSupportFragmentManager(), "seleccionar_bebida");
    }

    /**
     * Equivalente a: seleccionarTamanoPrecio() en el código C.
     */
    private void mostrarSeleccionTamano(BebidaEntity bebida) {
        if (bebida.tieneTamanos()) {
            String[] opciones = {
                    String.format("Chico  - $%.2f", bebida.precioChico),
                    String.format("Grande - $%.2f", bebida.precioGrande)
            };
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Tamaño de " + bebida.nombre)
                    .setItems(opciones, (dialog, which) -> {
                        String tamano = which == 0 ? "Chico" : "Grande";
                        double precio = which == 0 ? bebida.precioChico : bebida.precioGrande;
                        mostrarDialogoCantidad(bebida, tamano, precio);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            mostrarDialogoCantidad(bebida, "Fijo", bebida.precioChico);
        }
    }

    /**
     * Equivalente a: leerEnteroRangoCancelar("Cantidad: ", 1, 999, &cantidad)
     */
    private void mostrarDialogoCantidad(BebidaEntity bebida, String tamano, double precio) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cantidad, null);
        com.google.android.material.textfield.TextInputEditText editCantidad =
                dialogView.findViewById(R.id.edit_cantidad);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Cantidad de " + bebida.nombre)
                .setMessage(tamano + " — $" + String.format("%.2f", precio) + " c/u")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String input = editCantidad.getText() != null
                            ? editCantidad.getText().toString().trim() : "";
                    try {
                        int cantidad = Integer.parseInt(input);
                        if (cantidad < 1 || cantidad > 999) {
                            Snackbar.make(binding.getRoot(),
                                    "La cantidad debe estar entre 1 y 999.", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        DetalleVentaEntity detalle = new DetalleVentaEntity(
                                0, bebida.id, bebida.nombre, tamano, cantidad, precio);
                        detalleAdapter.agregarDetalle(detalle);
                        actualizarTotal();
                    } catch (NumberFormatException e) {
                        Snackbar.make(binding.getRoot(),
                                "Escribe una cantidad válida.", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarTotal() {
        double total = detalleAdapter.calcularTotal();
        binding.textTotalVenta.setText(String.format("Total: $%.2f", total));
        boolean hayDetalles = detalleAdapter.getItemCount() > 0;
        binding.btnGuardarVenta.setEnabled(hayDetalles);
        binding.textSinBebidas.setVisibility(hayDetalles ? View.GONE : View.VISIBLE);
    }

    /**
     * Equivalente a: capturarDatosVenta() + escribirRegistro() en el código C.
     */
    private void guardarVenta() {
        List<DetalleVentaEntity> detalles = detalleAdapter.getDetalles();
        if (detalles.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Agrega al menos una bebida.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String cliente = binding.editCliente.getText() != null
                ? binding.editCliente.getText().toString().trim() : "";
        if (cliente.isEmpty()) cliente = "Sin nombre";

        double total = detalleAdapter.calcularTotal();
        VentaEntity venta = new VentaEntity(cliente, VentaRepository.getFechaHoy(), total);

        String clienteFinal = cliente;
        ventaRepository.insertarVentaConDetalles(venta, detalles, ventaId -> {
            runOnUiThread(() -> {
                Snackbar.make(binding.getRoot(),
                        String.format("Venta #%d guardada — Total: $%.2f", ventaId, total),
                        Snackbar.LENGTH_LONG).show();
                finish();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
