package patron;

import model.Celular;

public class PrecioEspecial implements EstrategiaPrecio {

    @Override
    public String getNombre() {
        return "Especial";
    }

    @Override
    public boolean aplicable(Celular celular) {
        return celular.getPrecioEspecial() != null;
    }

    @Override
    public double calcular(Celular celular) {
        if (!aplicable(celular)) {
            throw new IllegalStateException("El celular no tiene precio especial");
        }
        return celular.getPrecioEspecial();
    }
}
