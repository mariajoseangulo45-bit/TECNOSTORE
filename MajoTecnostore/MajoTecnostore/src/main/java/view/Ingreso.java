package view;

import java.util.function.Predicate;
import model.CategoriaGama;
import model.Celular;
import model.Cliente;
import util.Validador;

public class Ingreso {

    Validador v = new Validador();

    public Celular ingresarCelular() {
        String marca = v.validarTexto("Ingrese la marca", 50);
        String modelo = v.validarTexto("Ingrese el modelo", 80);
        CategoriaGama gama = escogerGama();
        double precio = v.validarDecimalPositivo("Ingrese el precio unitario");
        double precioMayor = v.validarDecimalPositivo("Ingrese el precio por mayor (no puede superar el unitario)");
        while (precioMayor > precio) {
            precioMayor = v.validarDecimalPositivo("Error, el precio por mayor no puede superar el unitario. Intente nuevamente!");
        }
        Double precioEspecial = ingresarPrecioEspecial();
        int stock = v.validarEnteroNoNegativo("Ingrese el stock");
        return new Celular(0, marca, modelo, null, gama, precio, precioMayor, precioEspecial, stock);
    }

    public Double ingresarPrecioEspecial() {
        double precioEspecial = v.validarDecimal("Ingrese el precio especial (0 si no tiene)");
        return precioEspecial == 0 ? null : precioEspecial;
    }

    // Los predicados permiten validar unicidad sin que la vista conozca la capa de datos.
    public Cliente ingresarCliente(Predicate<String> existeIdentificacion, Predicate<String> existeCorreo) {
        String identificacion = v.validarIdentificacion("Ingrese la identificacion");
        while (existeIdentificacion.test(identificacion)) {
            identificacion = v.validarIdentificacion("Error, la identificacion ya esta registrada. Intente nuevamente!");
        }
        String nombre = v.validarNombre("Ingrese el nombre");
        String apellido = v.validarNombre("Ingrese el apellido");
        String correo = v.validarCorreo("Ingrese el correo");
        while (existeCorreo.test(correo)) {
            correo = v.validarCorreo("Error, el correo ya esta registrado. Intente nuevamente!");
        }
        String telefono = v.validarTelefono("Ingrese el telefono");
        return new Cliente(0, null, nombre, apellido, identificacion, correo, telefono);
    }

    public CategoriaGama escogerGama() {
        StringBuilder menu = new StringBuilder("Escoja la gama\n");
        for (CategoriaGama g : CategoriaGama.values()) {
            menu.append(g.ordinal() + 1).append(". ").append(g.getNombre()).append("\n");
        }
        int op = v.validarEntero(menu.toString());
        while (op < 1 || op > CategoriaGama.values().length) {
            op = v.validarEntero("Opcion no encontrada! Intente nuevamente");
        }
        return CategoriaGama.values()[op - 1];
    }

    public int escogerOpcionGeneral() {

        return v.validarEntero("""
                               ===== TECNOSTORE =====
                               1. Celulares
                               2. Clientes
                               3. Ventas
                               4. Reportes
                               5. Salir
                               """);

    }

    public int escogerOpcionCelular() {

        return v.validarEntero("""
                               1. Agregar
                               2. Listar
                               3. Actualizar
                               4. Eliminar
                               5. Salir
                               """);

    }

    public int escogerOpcionActualizarCelular() {

        return v.validarEntero("""
                               1. Marca
                               2. Modelo
                               3. Sistema operativo
                               4. Gama
                               5. Precio unitario
                               6. Precio por mayor
                               7. Precio especial
                               8. Stock
                               9. Salir
                               """);

    }

    public int escogerOpcionCliente() {

        return v.validarEntero("""
                               1. Agregar
                               2. Listar
                               3. Actualizar
                               4. Eliminar
                               5. Salir
                               """);

    }

    public int escogerOpcionActualizarCliente() {

        return v.validarEntero("""
                               1. Nombre
                               2. Apellido
                               3. Correo
                               4. Telefono
                               5. Salir
                               """);

    }

    public int escogerOpcionVenta() {

        return v.validarEntero("""
                               1. Registrar venta
                               2. Listar ventas
                               3. Salir
                               """);

    }

    public int escogerOpcionTipoPrecio() {

        return v.validarEntero("""
                               Escoja el tipo de precio
                               1. Unitario
                               2. Mayor
                               3. Especial
                               """);

    }

    public int escogerOpcionReporte() {

        return v.validarEntero("""
                               1. Celulares con stock bajo
                               2. Top 3 mas vendidos
                               3. Ventas totales por mes
                               4. Generar reporte_ventas.txt
                               5. Salir
                               """);

    }

}
