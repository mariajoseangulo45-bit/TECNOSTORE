package controller;

import dao.CelularDAO;
import java.util.Map;
import model.Celular;
import util.Validador;
import view.Ingreso;
import view.MostrarCelular;

public class GestionCelular {

    CelularDAO dao = new CelularDAO();
    Ingreso ingreso = new Ingreso();
    MostrarCelular mostrarCelular = new MostrarCelular();
    Validador v = new Validador();

    // Devuelve el id de la version escogida, o 0 si no hay sistemas operativos registrados.
    private long cargarVersionSO() {
        Map<Long, String> versiones = dao.listarVersionesSO();
        if (versiones.isEmpty()) {
            System.out.println("No hay sistemas operativos registrados en la base de datos");
            return 0;
        }
        mostrarCelular.versionesSO(versiones);
        long id = v.validarEnteroGrande("Escoja el id del sistema operativo");
        while (!versiones.containsKey(id)) {
            id = v.validarEnteroGrande("Error, intente nuevamente!");
        }
        return id;
    }

    private void agregar() {
        long idVersionSo = cargarVersionSO();
        if (idVersionSo == 0) {
            return;
        }
        Celular celular = ingreso.ingresarCelular();
        celular.setIdVersionSo(idVersionSo);
        dao.crear(celular);
    }

    private void listar() {
        mostrarCelular.lista(dao.listar());
    }

    private void menuActualizar(Celular celular) {
        boolean cancelar = false;
        int op = ingreso.escogerOpcionActualizarCelular();
        try {
            switch (op) {
                case 1 -> {
                    celular.setMarca(v.validarTexto("Ingrese la nueva marca", 50));
                }
                case 2 -> {
                    celular.setModelo(v.validarTexto("Ingrese el nuevo modelo", 80));
                }
                case 3 -> {
                    long idVersionSo = cargarVersionSO();
                    if (idVersionSo == 0) {
                        cancelar = true;
                    } else {
                        celular.setIdVersionSo(idVersionSo);
                    }
                }
                case 4 -> {
                    celular.setGama(ingreso.escogerGama());
                }
                case 5 -> {
                    celular.setPrecio(v.validarDecimalPositivo("Ingrese el nuevo precio unitario"));
                }
                case 6 -> {
                    celular.setPrecioMayor(v.validarDecimalPositivo("Ingrese el nuevo precio por mayor"));
                }
                case 7 -> {
                    celular.setPrecioEspecial(ingreso.ingresarPrecioEspecial());
                }
                case 8 -> {
                    celular.setStock(v.validarEnteroNoNegativo("Ingrese el nuevo stock"));
                }
                case 9 -> {
                    System.out.println("Operacion cancelada");
                    cancelar = true;
                }
                default -> {
                    System.out.println("Opcion no encontrada!");
                    cancelar = true;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            cancelar = true;
        }
        if (cancelar == false) {
            dao.actualizar(celular);
        }
    }

    private void actualizar() {
        listar();
        long id = v.validarEnteroGrande("Ingrese el id a actualizar");
        Celular entidadActualizar = dao.buscar(id);
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
        } else if (v.validarSiNo("¿Desea eliminar el celular? (s/n)")) {
            dao.eliminar(id);
        } else {
            System.out.println("Operacion cancelada correctamente!");
        }
    }

    public void menu() {
        int op;
        do {
            op = ingreso.escogerOpcionCelular();
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
