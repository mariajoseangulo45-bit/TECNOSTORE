package model;

public enum CategoriaGama {

    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta");

    private final String nombre;

    CategoriaGama(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public static CategoriaGama desdeNombre(String nombre) {
        for (CategoriaGama g : values()) {
            if (g.nombre.equalsIgnoreCase(nombre)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Gama no valida: " + nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
