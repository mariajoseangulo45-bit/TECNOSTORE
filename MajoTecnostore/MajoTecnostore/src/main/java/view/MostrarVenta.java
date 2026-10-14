package view;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import model.ItemVenta;
import model.Venta;
import util.ReporteUtils;

public class MostrarVenta {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void lista(List<Venta> ventas) {
        for (Venta v : ventas) {
            venta(v);
        }
    }

    public void venta(Venta venta) {
        System.out.println("*************************");
        System.out.println("Venta #" + venta.getId() + "   Fecha: " + venta.getFecha().format(FORMATO_FECHA));
        System.out.println("Cliente: " + venta.getCliente().getNombreCompleto()
                + " (" + venta.getCliente().getIdentificacion() + ")");
        for (ItemVenta item : venta.getItems()) {
            System.out.println(String.format("   %-28s x%-3d %-9s %s",
                    item.getCelular().getMarca() + " " + item.getCelular().getModelo(),
                    item.getCantidad(), item.getEstrategia().getNombre(),
                    ReporteUtils.formatoMoneda(item.getSubtotal())));
        }
        System.out.println("   Subtotal: " + ReporteUtils.formatoMoneda(venta.getSubtotal()));
        System.out.println("   IVA:      " + ReporteUtils.formatoMoneda(venta.getIva()));
        System.out.println("   TOTAL:    " + ReporteUtils.formatoMoneda(venta.getTotal()));
    }

    public void empleados(Map<Long, String> empleados) {
        empleados.forEach((id, nombre) -> System.out.println(id + ". " + nombre));
    }
}
