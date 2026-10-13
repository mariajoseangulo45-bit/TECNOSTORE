package patron;

import model.Celular;

public class PrecioMayor implements EstrategiaPrecio {

    @Override
    public String getNombre() {
        return "Mayor";
    }

    @Override
    public double calcular(Celular celular) {
        return celular.getPrecioMayor();
    }
}
