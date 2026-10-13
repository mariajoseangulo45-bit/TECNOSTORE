package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Celular;
import model.Cliente;
import model.ItemVenta;
import model.Venta;
import patron.EstrategiaPrecio;

public class VentaDAO {

    ConexionDB c = ConexionDB.getInstancia();

    private static final String SELECT_VENTAS = "select v.id, v.id_empleado, v.fecha, v.total, c.id as cliente_id, "
            + "td.codigo as tipo_documento, p.nombre, p.apellido, p.identificacion, p.correo, p.telefono "
            + "from ventas v join clientes c on c.id = v.id_cliente "
            + "join personas p on p.id = c.id_persona "
            + "join tipo_documento td on td.id = p.id_tipo_documento "
            + "order by v.fecha, v.id";

    private static final String SELECT_DETALLES = "select d.id_venta, d.cantidad, d.subtotal, tp.nombre as tipo_precio, "
            + CelularDAO.COLUMNAS
            + "from detalle_venta d join tipo_precio tp on tp.id = d.id_tipo_precio "
            + "join celulares ce on ce.id = d.id_celular "
            + CelularDAO.JOINS
            + "order by d.id";

    private void insertarVenta(Connection con, Venta venta) throws SQLException {
        String sql = "insert into ventas (id_cliente, id_empleado, fecha, total) values (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, venta.getCliente().getId());
            ps.setLong(2, venta.getIdEmpleado());
            ps.setTimestamp(3, Timestamp.valueOf(venta.getFecha()));
            ps.setDouble(4, venta.getTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                venta.setId(rs.getLong(1));
            }
        }
    }

    private void insertarDetalle(Connection con, long idVenta, ItemVenta item) throws SQLException {
        String sql = "insert into detalle_venta (id_venta, id_celular, id_tipo_precio, cantidad, precio_unitario, "
                + "precio_mayor, precio_especial, subtotal) "
                + "values (?,?,(select id from tipo_precio where nombre=?),?,?,?,?,?)";
        Celular celular = item.getCelular();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idVenta);
            ps.setLong(2, celular.getId());
            ps.setString(3, item.getEstrategia().getNombre());
            ps.setInt(4, item.getCantidad());
            ps.setDouble(5, celular.getPrecio());
            ps.setDouble(6, celular.getPrecioMayor());
            if (celular.getPrecioEspecial() == null) {
                ps.setNull(7, Types.DECIMAL);
            } else {
                ps.setDouble(7, celular.getPrecioEspecial());
            }
            ps.setDouble(8, item.getSubtotal());
            ps.executeUpdate();
        }
    }

    // La condicion "stock >= ?" evita vender mas unidades de las que existen, incluso con ventas simultaneas.
    private void descontarStock(Connection con, ItemVenta item) throws SQLException {
        Celular celular = item.getCelular();
        try (PreparedStatement ps = con.prepareStatement(
                "update celulares set stock = stock - ? where id=? and stock >= ?")) {
            ps.setInt(1, item.getCantidad());
            ps.setLong(2, celular.getId());
            ps.setInt(3, item.getCantidad());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Stock insuficiente para " + celular.getMarca() + " " + celular.getModelo());
            }
        }
        int stockNuevo;
        try (PreparedStatement ps = con.prepareStatement("select stock from celulares where id=?")) {
            ps.setLong(1, celular.getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                stockNuevo = rs.getInt(1);
            }
        }
        CelularDAO.registrarAuditoria(con, celular.getId(), "CAMBIO_STOCK",
                "Stock de " + (stockNuevo + item.getCantidad()) + " a " + stockNuevo);
        if (stockNuevo < Celular.STOCK_BAJO) {
            CelularDAO.registrarAuditoria(con, celular.getId(), "ALERTA_STOCK_BAJO",
                    CelularDAO.textoStockBajo(stockNuevo));
        }
    }

    public boolean crear(Venta venta) {
        try (Connection con = c.conexion()) {
            con.setAutoCommit(false);
            try {
                insertarVenta(con, venta);
                for (ItemVenta item : venta.getItems()) {
                    insertarDetalle(con, venta.getId(), item);
                    descontarStock(con, item);
                }
                con.commit();
                System.out.println("Venta registrada correctamente! Id de la venta: " + venta.getId());
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar la venta, no se guardo ningun cambio: " + e.getMessage());
        }
        return false;
    }

    public List<Venta> listar() {
        Map<Long, Venta> ventas = new LinkedHashMap<>();
        try (Connection con = c.conexion()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_VENTAS); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente(rs.getLong("cliente_id"), rs.getString("tipo_documento"),
                            rs.getString("nombre"), rs.getString("apellido"), rs.getString("identificacion"),
                            rs.getString("correo"), rs.getString("telefono"));
                    Venta venta = new Venta(rs.getLong("id"), cliente, rs.getLong("id_empleado"),
                            rs.getTimestamp("fecha").toLocalDateTime(), rs.getDouble("total"));
                    ventas.put(venta.getId(), venta);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(SELECT_DETALLES); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta venta = ventas.get(rs.getLong("id_venta"));
                    if (venta != null) {
                        int cantidad = rs.getInt("cantidad");
                        venta.agregarItem(new ItemVenta(CelularDAO.mapear(rs), cantidad,
                                EstrategiaPrecio.desdeNombre(rs.getString("tipo_precio")),
                                rs.getDouble("subtotal") / cantidad));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return new ArrayList<>(ventas.values());
    }

    public Map<Long, String> listarEmpleadosActivos() {
        Map<Long, String> empleados = new LinkedHashMap<>();
        String sql = "select e.id, p.nombre, p.apellido from empleado e "
                + "join personas p on p.id = e.id_persona where e.activo = true order by e.id";
        try (Connection con = c.conexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                empleados.put(rs.getLong("id"), rs.getString("nombre") + " " + rs.getString("apellido"));
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return empleados;
    }
}
