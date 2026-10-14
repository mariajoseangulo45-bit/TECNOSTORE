package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexionDB {

    private static ConexionDB instancia;

    private final String url;
    private final String usuario;
    private final String clave;

    private ConexionDB() {
        this.url = System.getenv().getOrDefault("TECNOSTORE_DB_URL", "jdbc:mysql://localhost:3306/tecnostore_db");
        this.usuario = System.getenv().getOrDefault("TECNOSTORE_DB_USER", "root");
        this.clave = System.getenv().getOrDefault("TECNOSTORE_DB_PASSWORD", "Lavidaesbella2040.");
    }

    public static synchronized ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection conexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }
}