package controller;

import dao.CelularDAO;
import dao.ClienteDAO;
import dao.VentaDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Celular;
import model.Cliente;
import model.ItemVenta;
import model.Venta;
import patron.EstrategiaPrecio;
import util.Validador;
import view.Ingreso;
import view.MostrarCelular;
import view.MostrarCliente;
import view.MostrarVenta;

public class GestorVenta {

    VentaDAO dao = new VentaDAO();
    CelularDAO daoCelular = new CelularDAO();
    ClienteDAO daoCliente = new ClienteDAO();
    Ingreso ingreso = new Ingreso();
    MostrarVenta mostrarVenta = new MostrarVenta();
    MostrarCelular mostrarCelular = new MostrarCelular();
    MostrarCliente mostrarCliente = new MostrarCliente();
    Validador v = new Validador();

    // Devuelve null si la persona cancela (id 0)
    private Cliente cargarCliente() {
        mostrarCliente.lista(daoCliente.listar());
        long id = v.validarEnteroGrande("Escoja el id del cliente (0 para cancelar)");
        while (id != 0) {
            Cliente cliente = daoCliente.buscar(id);
            if (cliente != null) {
                return cliente;
            }
            id = v.validarEnteroGrande("Error, intente nuevamente! (0 para cancelar)");
        }
        return null;
    }

    // Devuelve 0 si la persona cancela
    private long cargarEmpleado() {
        Map<Long, String> empleados = dao.listarEmpleadosActivos();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados activos para registrar la venta");
            return 0;
        }
        mostrarVenta.empleados(empleados);
        long id = v.validarEnteroGrande("Escoja el id del empleado que realiza la venta (0 para cancelar)");
        while (id != 0 && !empleados.containsKey(id)) {
            id = v.validarEnteroGrande("Error, intente nuevamente! (0 para cancelar)");
        }
        return id;
    }

    // Devuelve null si la persona cancela (id 0)
    private Celular cargarCelular(List<Celular> disponibles) {
        mostrarCelular.lista(disponibles);
        long id = v.validarEnteroGrande("Escoja el id del celular (0 para cancelar)");
        while (id != 0) {
            for (Celular c : disponibles) {
                if (c.getId() == id) {
                    return c;
                }
            }
            id = v.validarEnteroGrande("Error, intente nuevamente! (0 para cancelar)");
        }
        return null;
    }

    private EstrategiaPrecio cargarEstrategia(Celular celular) {
        EstrategiaPrecio estrategia = EstrategiaPrecio.desdeOpcion(ingreso.escogerOpcionTipoPrecio());
        while (estrategia == null || !estrategia.aplicable(celular)) {
            System.out.println(estrategia == null ? "Opcion no encontrada!" : "Este celular no tiene precio especial");
            estrategia = EstrategiaPrecio.desdeOpcion(ingreso.escogerOpcionTipoPrecio());
        }
        return estrategia;
    }

    private void agregarItem(Venta venta, List<Celular> disponibles) {
        Celular celular = cargarCelular(disponibles);
        if (celular == null) {
            return;
        }
        Optional<ItemVenta> existente = venta.buscarItem(celular.getId());
        int unidades = celular.getStock() - existente.map(ItemVenta::getCantidad).orElse(0);
        if (unidades <= 0) {
            System.out.println("Ya agrego a la venta todo el stock disponible de este celular");
            return;
        }
        int cantidad = v.validarEnteroPositivo("Ingrese la cantidad (disponibles: " + unidades + ")");
        while (cantidad > unidades) {
            cantidad = v.validarEnteroPositivo("Error, solo hay " + unidades + " unidades disponibles. Intente nuevamente!");
        }
        if (existente.isPresent()) {
            existente.get().aumentarCantidad(cantidad);
            System.out.println("El celular ya estaba en la venta, se sumo la cantidad al item existente");
        } else {
            venta.agregarItem(new ItemVenta(celular, cantidad, cargarEstrategia(celular)));
        }
    }

    private void registrar() {
        if (daoCliente.listar().isEmpty()) {
            System.out.println("Debe registrar minimo 1 cliente para realizar una venta");
            return;
        }
        List<Celular> disponibles = daoCelular.listar().stream().filter(c -> c.getStock() > 0).toList();
        if (disponibles.isEmpty()) {
            System.out.println("No hay celulares con stock disponible");
            return;
        }
        Cliente cliente = cargarCliente();
        if (cliente == null) {
            System.out.println("Venta cancelada");
            return;
        }
        long idEmpleado = cargarEmpleado();
        if (idEmpleado == 0) {
            System.out.println("Venta cancelada");
            return;
        }
        Venta venta = new Venta(0, cliente, idEmpleado, LocalDateTime.now().withNano(0), 0);
        boolean seguir;
        do {
            agregarItem(venta, disponibles);
            seguir = v.validarSiNo("¿Desea agregar otro celular a la venta? (s/n)");
        } while (seguir);
        if (venta.getItems().isEmpty()) {
            System.out.println("Venta cancelada, no se agrego ningun celular");
            return;
        }
        venta.calcularTotal();
        mostrarVenta.venta(venta);
        if (v.validarSiNo("¿Desea confirmar la venta? (s/n)")) {
            dao.crear(venta);
        } else {
            System.out.println("Venta cancelada");
        }
    }

    private void listar() {
        mostrarVenta.lista(dao.listar());
    }

    public void menu() {
        int op;
        do {
            op = ingreso.escogerOpcionVenta();
            switch (op) {
                case 1 ->
                    registrar();
                case 2 ->
                    listar();
                case 3 ->
                    System.out.println("Saliendo...");
                default ->
                    System.out.println("Opcion no encontrada!");
            }
        } while (op != 3);
    }
}
