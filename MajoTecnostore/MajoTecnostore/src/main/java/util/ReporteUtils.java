package util;
 
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import model.Celular;
import model.ItemVenta;
import model.Venta;
 
public class ReporteUtils {
 
    // Convierte 1849700 en $1.849.700,00
    public static String formatoMoneda(double valor) {
        return String.format(Locale.of("es", "CO"), "$%,.2f", valor);
    }
 
    // Reporte 1: celulares con menos de 5 unidades, del que tiene menos al que tiene mas
    public static List<Celular> celularesStockBajo(List<Celular> celulares) {
        return celulares.stream()
                .filter(c -> c.getStock() < Celular.STOCK_BAJO)
                .sorted((a, b) -> a.getStock() - b.getStock())
                .toList();
    }
 
    // Reporte 2: los celulares mas vendidos (por unidades)
    public static Map<String, Integer> topCelularesMasVendidos(List<Venta> ventas, int limite) {
        // 1. Sumar las unidades vendidas de cada celular
        Map<String, Integer> unidades = ventas.stream()
                .flatMap(v -> v.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getCelular().getMarca() + " " + item.getCelular().getModelo(),
                        Collectors.summingInt(ItemVenta::getCantidad)));
 
        // 2. Ordenar de mayor a menor y quedarse con los primeros
        Map<String, Integer> top = new LinkedHashMap<>();
        new TreeMap<>(unidades).entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limite)
                .forEach(e -> top.put(e.getKey(), e.getValue()));
        return top;
    }
 
    // Reporte 3: total vendido en cada mes
    public static Map<YearMonth, Double> ventasTotalesPorMes(List<Venta> ventas) {
        Map<YearMonth, Double> porMes = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> YearMonth.from(v.getFecha()),
                        Collectors.summingDouble(Venta::getTotal)));
        return new TreeMap<>(porMes); // TreeMap deja los meses en orden
    }
}