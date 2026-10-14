package view;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import model.Celular;
import util.ReporteUtils;

public class MostrarReporte {

    public void stockBajo(List<Celular> celulares) {
        System.out.println("===== Celulares con stock bajo (menos de " + Celular.STOCK_BAJO + " unidades) =====");
        if (celulares.isEmpty()) {
            System.out.println("No hay celulares con stock bajo");
        }
        for (Celular c : celulares) {
            System.out.println(c.getMarca() + " " + c.getModelo() + " - Stock: " + c.getStock());
        }
    }

    public void topCelulares(Map<String, Integer> top) {
        System.out.println("===== Top 3 de celulares mas vendidos =====");
        if (top.isEmpty()) {
            System.out.println("Aun no hay ventas registradas");
        }
        int puesto = 1;
        for (Map.Entry<String, Integer> e : top.entrySet()) {
            System.out.println(puesto++ + ". " + e.getKey() + " - " + e.getValue() + " unidades");
        }
    }

    public void ventasPorMes(Map<YearMonth, Double> ventas) {
        System.out.println("===== Ventas totales por mes =====");
        if (ventas.isEmpty()) {
            System.out.println("Aun no hay ventas registradas");
        }
        ventas.forEach((mes, total) -> System.out.println(mes + ": " + ReporteUtils.formatoMoneda(total)));
    }
}
