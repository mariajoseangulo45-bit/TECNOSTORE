package patron;

import model.Celular;

public interface EstrategiaPrecio {

    // Debe coincidir con tipo_precio.nombre en la base de datos
    String getNombre();

    double calcular(Celular celular);

    default boolean aplicable(Celular celular) {
        return true;
    }

    static EstrategiaPrecio desdeOpcion(int opcion) {
        return switch (opcion) {
            case 1 -> new PrecioUnitario();
            case 2 -> new PrecioMayor();
            case 3 -> new PrecioEspecial();
            default -> null;
        };
    }

    static EstrategiaPrecio desdeNombre(String nombre) {
        return switch (nombre.toLowerCase()) {
            case "unitario" -> new PrecioUnitario();
            case "mayor" -> new PrecioMayor();
            case "especial" -> new PrecioEspecial();
            default -> throw new IllegalArgumentException("Tipo de precio no valido: " + nombre);
        };
    }
}