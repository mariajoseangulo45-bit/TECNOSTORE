package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Venta {

    public static final double IVA = 0.19;

    private long id;
    private final Cliente cliente;
    private final long idEmpleado;
    private final LocalDateTime fecha;
    private final List<ItemVenta> items = new ArrayList<>();
    private double total;

    public Venta(long id, Cliente cliente, long idEmpleado, LocalDateTime fecha, double total) {
        this.id = id;
        this.cliente = cliente;
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.total = total;
    }

    public void agregarItem(ItemVenta item) {
        items.add(item);
    }

    public Optional<ItemVenta> buscarItem(long idCelular) {
        return items.stream().filter((ItemVenta i) -> i.getCelular().getId() == idCelular).findFirst();
    }

    public double getSubtotal() {
        return redondear(items.stream().mapToDouble(ItemVenta::getSubtotal).sum());
    }

    // Total = subtotal + IVA del 19%. Debe llamarse antes de guardar la venta.
    public void calcularTotal() {
        double subtotal = getSubtotal();
        this.total = redondear(subtotal + subtotal * IVA);
    }

    // Se calcula como total - subtotal para respetar lo que realmente quedo guardado.
    public double getIva() {
        return redondear(total - getSubtotal());
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public long getIdEmpleado() {
        return idEmpleado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<ItemVenta> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotal() {
        return total;
    }
}
