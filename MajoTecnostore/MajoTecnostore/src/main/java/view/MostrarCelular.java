package view;

import java.util.List;
import java.util.Map;
import model.Celular;

public class MostrarCelular {

    public void lista(List<Celular> celulares) {
        for (Celular c : celulares) {
            System.out.println(c);
        }
    }

    public void versionesSO(Map<Long, String> versiones) {
        versiones.forEach((id, nombre) -> System.out.println(id + ". " + nombre));
    }
}