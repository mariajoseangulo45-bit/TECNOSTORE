package controller;

import dao.ClienteDAO;
import java.util.Map;
import model.Cliente;
import util.Validador;
import view.Ingreso;
import view.MostrarCliente;

public class GestionCliente {

    ClienteDAO dao = new ClienteDAO();
    Ingreso ingreso = new Ingreso();
    MostrarCliente mostrarCliente = new MostrarCliente();
    Validador v = new Validador();

    private String cargarTipoDocumento() {
        Map<String, String> tipos = dao.listarTiposDocumento();
        mostrarCliente.tiposDocumento(tipos);
        String codigo = v.validarTexto("Escoja el codigo del tipo de documento", 10).toUpperCase();
        while (!tipos.containsKey(codigo)) {
            codigo = v.validarTexto("Error, intente nuevamente!", 10).toUpperCase();
        }
        return codigo;
    }

    private void agregar() {
        String tipoDocumento = cargarTipoDocumento();
        Cliente cliente = ingreso.ingresarCliente(dao::existeIdentificacion, dao::existeCorreo);
        cliente.setTipoDocumento(tipoDocumento);
        dao.crear(cliente);
    }

    private void listar() {
        mostrarCliente.lista(dao.listar());
    }

    private void menuActualizar(Cliente cliente) {
        boolean cancelar = false;
        int op = ingreso.escogerOpcionActualizarCliente();
        switch (op) {
            case 1 -> {
                cliente.setNombre(v.validarNombre("Ingrese el nuevo nombre"));
            }
            case 2 -> {
                cliente.setApellido(v.validarNombre("Ingrese el nuevo apellido"));
            }
            case 3 -> {
                String correo = v.validarCorreo("Ingrese el nuevo correo");
                while (!correo.equalsIgnoreCase(cliente.getCorreo()) && dao.existeCorreo(correo)) {
                    correo = v.validarCorreo("Error, el correo ya esta registrado. Intente nuevamente!");
                }
                cliente.setCorreo(correo);
            }
            case 4 -> {
                cliente.setTelefono(v.validarTelefono("Ingrese el nuevo telefono"));
            }
            case 5 -> {
                System.out.println("Operacion cancelada");
                cancelar = true;
            }
            default -> {
                System.out.println("Opcion no encontrada!");
                cancelar = true;
            }
        }
        if (cancelar == false) {
            dao.actualizar(cliente);
        }
    }

    private void actualizar() {
        listar();
        long id = v.validarEnteroGrande("Ingrese el id a actualizar");
        Cliente entidadActualizar = dao.buscar(id);
        if (entidadActualizar == null) {
            System.out.println("No existe dicha entidad");
        } else {
            menuActualizar(entidadActualizar);
        }
    }

    private void eliminar() {
        listar();
        long id = v.validarEnteroGrande("Ingrese el id a eliminar");
        if (dao.buscar(id) == null) {
            System.out.println("No existe dicha entidad");
        } else if (v.validarSiNo("¿Desea eliminar el cliente? (s/n)")) {
            dao.eliminar(id);
        } else {
            System.out.println("Operacion cancelada correctamente!");
        }
    }

    public void menu() {
        int op;
        do {
            op = ingreso.escogerOpcionCliente();
            switch (op) {
                case 1 ->
                    agregar();
                case 2 ->
                    listar();
                case 3 ->
                    actualizar();
                case 4 ->
                    eliminar();
                case 5 ->
                    System.out.println("Saliendo...");
                default ->
                    System.out.println("Opcion no encontrada!");
            }
        } while (op != 5);
    }
}
