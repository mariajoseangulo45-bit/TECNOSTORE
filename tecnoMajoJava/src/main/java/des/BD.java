package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author maria
 */
public class BD {
     public Connection conecciones() {
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tecnostore_db", "root", "Lavidaesbella2040.");
        } catch (SQLException e) {
            System.out.println(e);
        }
        return c;
    }
    
}
