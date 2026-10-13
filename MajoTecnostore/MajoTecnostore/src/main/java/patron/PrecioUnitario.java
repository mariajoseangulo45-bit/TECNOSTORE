package patron;

import model.Celular;

public class PrecioUnitario implements EstrategiaPrecio {

    @Override
    public String getNombre() {
        return "Unitario";
    }

    @Override
    public double calcular(Celular celular) {
        return celular.getPrecio();
    }
}
