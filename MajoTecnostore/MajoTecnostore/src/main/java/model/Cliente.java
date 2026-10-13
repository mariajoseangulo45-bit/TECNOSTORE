package model;

public class Cliente extends Persona {

    private long id;

    public Cliente(long id, String tipoDocumento, String nombre, String apellido, String identificacion,
            String correo, String telefono) {
        super(tipoDocumento, nombre, apellido, identificacion, correo, telefono);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return """
               *************************
               Id:             %s
               Nombre:         %s
               Identificacion: %s %s
               Correo:         %s
               Telefono:       %s
               """.formatted(id, getNombreCompleto(), getTipoDocumento(), getIdentificacion(),
                getCorreo(), getTelefono());
    }
}
