package controller;

import dao.CelularDAO;
import dao.VentaDAO;
import util.ArchivoUtils;
import util.ReporteUtils;
import view.Ingreso;
import view.MostrarReporte;

public class GestorReportes {

    CelularDAO daoCelular = new CelularDAO();
    VentaDAO daoVenta = new VentaDAO();
    Ingreso ingreso = new Ingreso();
    MostrarReporte mostrarReporte = new MostrarReporte();

    public void menu() {
        int op;
        do {
            op = ingreso.escogerOpcionReporte();
            switch (op) {
                case 1 ->
                    mostrarReporte.stockBajo(ReporteUtils.celularesStockBajo(daoCelular.listar()));
                case 2 ->
                    mostrarReporte.topCelulares(ReporteUtils.topCelularesMasVendidos(daoVenta.listar(), 3));
                case 3 ->
                    mostrarReporte.ventasPorMes(ReporteUtils.ventasTotalesPorMes(daoVenta.listar()));
                case 4 ->
                    ArchivoUtils.generarReporteVentas(daoVenta.listar(), "reporte_ventas.txt");
                case 5 ->
                    System.out.println("Saliendo...");
                default ->
                    System.out.println("Opcion no encontrada!");
            }
        } while (op != 5);
    }
}
