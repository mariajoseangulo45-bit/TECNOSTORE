package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Cliente;

public class ClienteDAO {

    ConexionDB c = ConexionDB.getInstancia();

    private static final String SELECT = "select c.id, td.codigo as tipo_documento, p.nombre, p.apellido, "
            + "p.identificacion, p.correo, p.telefono "
            + "from clientes c join personas p on p.id = c.id_persona "
            + "join tipo_documento td on td.id = p.id_tipo_documento ";

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(rs.getLong("id"), rs.getString("tipo_documento"), rs.getString("nombre"),
                rs.getString("apellido"), rs.getString("identificacion"), rs.getString("correo"),
                rs.getString("telefono"));
    }

    private boolean existe(String sql, String valor) {
        try (Connection con = c.conexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return false;
    }

    public boolean existeIdentificacion(String identificacion) {
        return existe("select 1 from personas where identificacion=?", identificacion);
    }

    public boolean existeCorreo(String correo) {
        return existe("select 1 from personas where correo=?", correo);
    }

    public boolean crear(Cliente cliente) {
        try (Connection con = c.conexion()) {
            con.setAutoCommit(false);
            try {
                long idPersona;
                String sqlPersona = "insert into personas (id_tipo_documento, identificacion, nombre, apellido, "
                        + "correo, telefono) values ((select id from tipo_documento where codigo=?),?,?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, cliente.getTipoDocumento());
                    ps.setString(2, cliente.getIdentificacion());
                    ps.setString(3, cliente.getNombre());
                    ps.setString(4, cliente.getApellido());
                    ps.setString(5, cliente.getCorreo());
                    ps.setString(6, cliente.getTelefono());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        idPersona = rs.getLong(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement("insert into clientes (id_persona) values (?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, idPersona);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            cliente.setId(rs.getLong(1));
                        }
                    }
                }
                con.commit();
                System.out.println("Cliente creado correctamente!");
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: la identificacion o el correo ya estan registrados. " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error en el ingreso de datos: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "update personas p join clientes c on c.id_persona = p.id "
                + "set p.nombre=?, p.apellido=?, p.correo=?, p.telefono=? where c.id=?";
        try (Connection con = c.conexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setLong(5, cliente.getId());
            ps.executeUpdate();
            System.out.println("Cliente actualizado correctamente!");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: el correo ya esta registrado. " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error en la actualizacion de datos: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(long id) {
        try (Connection con = c.conexion()) {
            long idPersona;
            try (PreparedStatement ps = con.prepareStatement("select id_persona from clientes where id=?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Operacion cancelada, el id no existe");
                        return false;
                    }
                    idPersona = rs.getLong(1);
                }
            }
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("delete from clientes where id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("delete from personas where id=?")) {
                    ps.setLong(1, idPersona);
                    ps.executeUpdate();
                }
                con.commit();
                System.out.println("Cliente eliminado correctamente!");
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Este cliente no puede ser eliminado, tiene ventas asociadas!");
        } catch (SQLException e) {
            System.out.println("Error en la eliminacion de datos: " + e.getMessage());
        }
        return false;
    }

    public Cliente buscar(long id) {
        try (Connection con = c.conexion(); PreparedStatement ps = con.prepareStatement(SELECT + "where c.id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = c.conexion();
                PreparedStatement ps = con.prepareStatement(SELECT + "order by c.id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, String> listarTiposDocumento() {
        Map<String, String> tipos = new LinkedHashMap<>();
        try (Connection con = c.conexion();
                PreparedStatement ps = con.prepareStatement("select codigo, descripcion from tipo_documento order by id");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tipos.put(rs.getString("codigo"), rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return tipos;
    }
}
