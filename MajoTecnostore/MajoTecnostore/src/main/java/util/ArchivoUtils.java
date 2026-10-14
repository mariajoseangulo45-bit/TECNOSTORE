package util;
 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.ItemVenta;
import model.Venta;
 
public class ArchivoUtils {
 
    public static boolean generarReporteVentas(List<Venta> ventas, String ruta) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        double totalGeneral = 0;
 
        // try-with-resources: el archivo se cierra solo al terminar
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            pw.println("REPORTE DE VENTAS - TECNOSTORE");
            pw.println("======================================================");
            for (Venta venta : ventas) {
                pw.println("Venta #" + venta.getId() + " | " + venta.getFecha().format(formato)
                        + " | Cliente: " + venta.getCliente().getNombreCompleto());
                for (ItemVenta item : venta.getItems()) {
                    pw.println("   - " + item.getCelular().getMarca() + " " + item.getCelular().getModelo()
                            + " x" + item.getCantidad() + " = " + ReporteUtils.formatoMoneda(item.getSubtotal()));
                }
                pw.println("   Total de la venta: " + ReporteUtils.formatoMoneda(venta.getTotal()));
                pw.println("------------------------------------------------------");
                totalGeneral += venta.getTotal();
            }
            pw.println("Ventas realizadas: " + ventas.size());
            pw.println("Total vendido: " + ReporteUtils.formatoMoneda(totalGeneral));
            System.out.println("Reporte generado correctamente en: " + new File(ruta).getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.out.println("Error al generar el archivo: " + e.getMessage());
        }
        return false;
    }
}
