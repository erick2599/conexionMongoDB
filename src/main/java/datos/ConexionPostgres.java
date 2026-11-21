package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionPostgres {
    private static final String VARIABLE = "SUPABASE_DB_URL";

    private ConexionPostgres() { }

    public static Connection obtener() throws SQLException {
        String url = System.getenv(VARIABLE);
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno " + VARIABLE);
        }
        return DriverManager.getConnection(url);
    }
}
