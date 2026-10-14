package view;

import java.util.List;
import java.util.Map;
import model.Cliente;

public class MostrarCliente {

    public void lista(List<Cliente> clientes) {
        for (Cliente c : clientes) {
            System.out.println(c);
        }
    }

    public void tiposDocumento(Map<String, String> tipos) {
        tipos.forEach((codigo, descripcion) -> System.out.println(codigo + " - " + descripcion));
    }
}
