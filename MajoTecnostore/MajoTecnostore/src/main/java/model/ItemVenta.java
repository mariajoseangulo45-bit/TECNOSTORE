package model;

import patron.EstrategiaPrecio;

public class ItemVenta {

    private Celular celular;
    private int cantidad;
    private EstrategiaPrecio estrategia;
    private double precioAplicado;

    public ItemVenta(Celular celular, int cantidad, EstrategiaPrecio estrategia) {
        this(celular, cantidad, estrategia, estrategia.calcular(celular));
    }

    public ItemVenta(Celular celular, int cantidad, EstrategiaPrecio estrategia, double precioAplicado) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.celular = celular;
        this.cantidad = cantidad;
        this.estrategia = estrategia;
        this.precioAplicado = precioAplicado;
    }

    public void aumentarCantidad(int extra) {
        if (extra <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad += extra;
    }

    public double getSubtotal() {
        return Math.round(precioAplicado * cantidad * 100.0) / 100.0;
    }

    public Celular getCelular() {
        return celular;
    }

    public int getCantidad() {
        return cantidad;
    }

    public EstrategiaPrecio getEstrategia() {
        return estrategia;
    }

    public double getPrecioAplicado() {
        return precioAplicado;
    }
}
