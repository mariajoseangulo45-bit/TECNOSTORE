package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import model.CategoriaGama;
import model.Celular;

public class CelularDAO {

    ConexionDB c = ConexionDB.getInstancia();

    // Columnas y joins compartidos con VentaDAO para armar un Celular completo
    static final String COLUMNAS = "ce.id, m.nombre as marca, ce.modelo, so.nombre as sistema_operativo, "
            + "vs.version, ce.id_version_so, g.nombre as gama, ce.precio_unitario, ce.precio_mayor, "
            + "ce.precio_especial, ce.stock ";
    static final String JOINS = "join marca m on m.id = ce.id_marca "
            + "join version_sistema_operativo vs on vs.id = ce.id_version_so "
            + "join sistema_operativo so on so.id = vs.id_sistema_operativo "
            + "join gama g on g.id = ce.id_gama ";
    private static final String SELECT = "select " + COLUMNAS + "from celulares ce " + JOINS;

    static Celular mapear(ResultSet rs) throws SQLException {
        double especial = rs.getDouble("precio_especial");
        Double precioEspecial = rs.wasNull() ? null : especial;
        Celular celular = new Celular(rs.getLong("id"), rs.getString("marca"), rs.getString("modelo"),
                rs.getString("sistema_operativo") + " " + rs.getString("version"),
                CategoriaGama.desdeNombre(rs.getString("gama")),
                rs.getDouble("precio_unitario"), rs.getDouble("precio_mayor"), precioEspecial,
                rs.getInt("stock"));
        celular.setIdVersionSo(rs.getLong("id_version_so"));
        return celular;
    }

    static void registrarAuditoria(Connection con, Long idCelular, String accion, String detalle) throws SQLException {
        String sql = "insert into auditoria_celular (id_celular, id_tipo_accion, detalle_cambio) "
                + "values (?, (select id from tipo_accion where nombre=?), ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (idCelular == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setLong(1, idCelular);
            }
            ps.setString(2, accion);
            ps.setString(3, detalle);
            ps.executeUpdate();
        }
    }

    private long obtenerOCrearMarca(Connection con, String nombre) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("select id from marca where nombre=?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        try (PreparedStatement ps = con.prepareStatement("insert into marca (nombre) values (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    static String textoStockBajo(int stock) {
        return stock == 1 ? "Queda solo 1 unidad" : "Quedan solo " + stock + " unidades";
    }

    private String monto(Double valor) {
        if (valor == null) {
            return "sin precio";
        }
        return valor == Math.rint(valor) ? String.valueOf(valor.longValue()) : String.valueOf(valor);
    }

    private void auditarCambios(Connection con, Celular antes, Celular despues) throws SQLException {
        long id = despues.getId();
        if (antes.getPrecio() != despues.getPrecio()) {
            registrarAuditoria(con, id, "ACTUALIZACION_PRECIO",
                    "Precio unitario de " + monto(antes.getPrecio()) + " a " + monto(despues.getPrecio()));
        }
        if (antes.getPrecioMayor() != despues.getPrecioMayor()) {
            registrarAuditoria(con, id, "ACTUALIZACION_PRECIO",
                    "Precio por mayor de " + monto(antes.getPrecioMayor()) + " a " + monto(despues.getPrecioMayor()));
        }
        if (!Objects.equals(antes.getPrecioEspecial(), despues.getPrecioEspecial())) {
            registrarAuditoria(con, id, "ACTUALIZACION_PRECIO",
                    "Precio especial de " + monto(antes.getPrecioEspecial()) + " a " + monto(despues.getPrecioEspecial()));
        }
        if (antes.getStock() != despues.getStock()) {
            registrarAuditoria(con, id, "CAMBIO_STOCK",
                    "Stock de " + antes.getStock() + " a " + despues.getStock());
            if (despues.tieneStockBajo()) {
                registrarAuditoria(con, id, "ALERTA_STOCK_BAJO", textoStockBajo(despues.getStock()));
            }
        }
    }

    public boolean crear(Celular celular) {
        try (Connection con = c.conexion()) {
            con.setAutoCommit(false);
            try {
                long idMarca = obtenerOCrearMarca(con, celular.getMarca());
                String sql = "insert into celulares (id_marca, id_version_so, id_gama, modelo, precio_unitario, "
                        + "precio_mayor, precio_especial, stock) "
                        + "values (?,?,(select id from gama where nombre=?),?,?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, idMarca);
                    ps.setLong(2, celular.getIdVersionSo());
                    ps.setString(3, celular.getGama().getNombre());
                    ps.setString(4, celular.getModelo());
                    ps.setDouble(5, celular.getPrecio());
                    ps.setDouble(6, celular.getPrecioMayor());
                    if (celular.getPrecioEspecial() == null) {
                        ps.setNull(7, Types.DECIMAL);
                    } else {
                        ps.setDouble(7, celular.getPrecioEspecial());
                    }
                    ps.setInt(8, celular.getStock());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            celular.setId(rs.getLong(1));
                        }
                    }
                }
                con.commit();
                System.out.println("Celular creado correctamente!");
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: ya existe un celular con esa marca y modelo, o hay un dato invalido. " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error en el ingreso de datos: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(Celular celular) {
        try (Connection con = c.conexion()) {
            con.setAutoCommit(false);
            try {
                Celular anterior = buscar(con, celular.getId());
                if (anterior == null) {
                    throw new SQLException("El celular no existe");
                }
                long idMarca = obtenerOCrearMarca(con, celular.getMarca());
                String sql = "update celulares set id_marca=?, id_version_so=?, "
                        + "id_gama=(select id from gama where nombre=?), modelo=?, precio_unitario=?, "
                        + "precio_mayor=?, precio_especial=?, stock=? where id=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setLong(1, idMarca);
                    ps.setLong(2, celular.getIdVersionSo());
                    ps.setString(3, celular.getGama().getNombre());
                    ps.setString(4, celular.getModelo());
                    ps.setDouble(5, celular.getPrecio());
                    ps.setDouble(6, celular.getPrecioMayor());
                    if (celular.getPrecioEspecial() == null) {
                        ps.setNull(7, Types.DECIMAL);
                    } else {
                        ps.setDouble(7, celular.getPrecioEspecial());
                    }
                    ps.setInt(8, celular.getStock());
                    ps.setLong(9, celular.getId());
                    ps.executeUpdate();
                }
                auditarCambios(con, anterior, celular);
                con.commit();
                System.out.println("Celular actualizado correctamente!");
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: ya existe un celular con esa marca y modelo, o hay un dato invalido. " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error en la actualizacion de datos: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(long id) {
        try (Connection con = c.conexion()) {
            Celular celular = buscar(con, id);
            if (celular == null) {
                System.out.println("Operacion cancelada, el id no existe");
                return false;
            }
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("delete from celulares where id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                registrarAuditoria(con, null, "ELIMINACION",
                        "Se eliminó " + celular.getMarca() + " " + celular.getModelo() + " (id " + id + ")");
                con.commit();
                System.out.println("Celular eliminado correctamente!");
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Este celular no puede ser eliminado, hay ventas que lo estan referenciando!");
        } catch (SQLException e) {
            System.out.println("Error en la eliminacion de datos: " + e.getMessage());
        }
        return false;
    }

    private Celular buscar(Connection con, long id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT + "where ce.id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Celular buscar(long id) {
        try (Connection con = c.conexion()) {
            return buscar(con, id);
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return null;
    }

    public List<Celular> listar() {
        List<Celular> lista = new ArrayList<>();
        try (Connection con = c.conexion();
                PreparedStatement ps = con.prepareStatement(SELECT + "order by ce.id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return lista;
    }

    public Map<Long, String> listarVersionesSO() {
        Map<Long, String> versiones = new LinkedHashMap<>();
        String sql = "select vs.id, so.nombre, vs.version from version_sistema_operativo vs "
                + "join sistema_operativo so on so.id = vs.id_sistema_operativo "
                + "order by so.nombre, vs.version desc";
        try (Connection con = c.conexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                versiones.put(rs.getLong("id"), rs.getString("nombre") + " " + rs.getString("version"));
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return versiones;
    }
}
