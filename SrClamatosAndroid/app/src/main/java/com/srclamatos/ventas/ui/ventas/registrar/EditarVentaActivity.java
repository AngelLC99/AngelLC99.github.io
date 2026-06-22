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
import com.srclamatos.ventas.data.entity.VentaConDetalles;
import com.srclamatos.ventas.data.entity.VentaEntity;
import com.srclamatos.ventas.data.repository.BebidaRepository;
import com.srclamatos.ventas.data.repository.VentaRepository;
import com.srclamatos.ventas.databinding.ActivityRegistrarVentaBinding;

import java.util.List;

/**
 * Equivalente a:
 *   MODO_AGREGAR   → agregarBebidasAVentaExistente() en el código C
 *   MODO_MODIFICAR → modificarVenta() en el código C
 */
public class EditarVentaActivity extends AppCompatActivity {

    public static final String EXTRA_VENTA_ID = "venta_id";
    public static final String EXTRA_MODO = "modo";
    public static final int MODO_AGREGAR = 1;
    public static final int MODO_MODIFICAR = 2;

    private ActivityRegistrarVentaBinding binding;
    private DetalleVentaAdapter detalleAdapter;
    private BebidaRepository bebidaRepository;
    private VentaRepository ventaRepository;
    private List<BebidaEntity> catalogoBebidas;
    private VentaConDetalles ventaOriginal;
    private int ventaId;
    private int modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrarVentaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ventaId = getIntent().getIntExtra(EXTRA_VENTA_ID, -1);
        modo = getIntent().getIntExtra(EXTRA_MODO, MODO_AGREGAR);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(modo == MODO_MODIFICAR
                    ? R.string.titulo_editar_venta
                    : R.string.titulo_agregar_bebidas);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bebidaRepository = new BebidaRepository(getApplication());
        ventaRepository = new VentaRepository(getApplication());

        setupRecyclerView();
        setupBotones();
        cargarDatosIniciales();
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
        binding.btnGuardarVenta.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatosIniciales() {
        bebidaRepository.getBebidasActivas().observe(this, bebidas -> {
            catalogoBebidas = bebidas;
        });

        ventaRepository.buscarPorId(ventaId, ventaConDetalles -> {
            if (ventaConDetalles == null) {
                runOnUiThread(() -> {
                    Snackbar.make(binding.getRoot(), "No se encontró la venta.", Snackbar.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }
            ventaOriginal = ventaConDetalles;
            runOnUiThread(() -> {
                binding.editCliente.setText(ventaConDetalles.venta.cliente);
                if (modo == MODO_MODIFICAR) {
                    detalleAdapter.setDetalles(ventaConDetalles.detalles);
                }
                actualizarTotal();
            });
        });
    }

    private void mostrarSeleccionBebida() {
        if (catalogoBebidas == null || catalogoBebidas.isEmpty()) {
            Snackbar.make(binding.getRoot(), "No hay bebidas en el catálogo.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        SeleccionarBebidaBottomSheet sheet = SeleccionarBebidaBottomSheet.newInstance(
                catalogoBebidas, this::mostrarSeleccionTamano);
        sheet.show(getSupportFragmentManager(), "seleccionar_bebida");
    }

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

    private void mostrarDialogoCantidad(BebidaEntity bebida, String tamano, double precio) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cantidad, null);
        com.google.android.material.textfield.TextInputEditText editCantidad =
                dialogView.findViewById(R.id.edit_cantidad);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Cantidad")
                .setMessage(bebida.nombre + " " + tamano + " — $" + String.format("%.2f", precio))
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String input = editCantidad.getText() != null
                            ? editCantidad.getText().toString().trim() : "";
                    try {
                        int cantidad = Integer.parseInt(input);
                        if (cantidad < 1) return;
                        DetalleVentaEntity detalle = new DetalleVentaEntity(
                                ventaId, bebida.id, bebida.nombre, tamano, cantidad, precio);
                        detalleAdapter.agregarDetalle(detalle);
                        actualizarTotal();
                    } catch (NumberFormatException ignored) {}
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarTotal() {
        double total = detalleAdapter.calcularTotal();
        binding.textTotalVenta.setText(String.format("Total: $%.2f", total));
        binding.textSinBebidas.setVisibility(
                detalleAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    private void guardarCambios() {
        if (ventaOriginal == null) return;
        List<DetalleVentaEntity> detalles = detalleAdapter.getDetalles();
        if (detalles.isEmpty()) {
            Snackbar.make(binding.getRoot(), "La venta debe tener al menos una bebida.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String cliente = binding.editCliente.getText() != null
                ? binding.editCliente.getText().toString().trim() : "";
        if (cliente.isEmpty()) cliente = "Sin nombre";

        VentaEntity venta = ventaOriginal.venta;
        venta.cliente = cliente;
        venta.total = detalleAdapter.calcularTotal();

        if (modo == MODO_MODIFICAR) {
            ventaRepository.actualizarVentaConDetalles(venta, detalles, () ->
                    runOnUiThread(() -> {
                        Snackbar.make(binding.getRoot(), "Venta actualizada correctamente.", Snackbar.LENGTH_SHORT).show();
                        finish();
                    })
            );
        } else {
            ventaRepository.agregarDetallesAVenta(venta, detalles, () ->
                    runOnUiThread(() -> {
                        Snackbar.make(binding.getRoot(), "Bebidas agregadas correctamente.", Snackbar.LENGTH_SHORT).show();
                        finish();
                    })
            );
        }
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
