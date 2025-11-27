package di.wordle.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:wordle.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializar() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL" +
                    ");";

            String sqlEstadisticas = "CREATE TABLE IF NOT EXISTS estadisticas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER NOT NULL," +
                    "partidas_jugadas INTEGER DEFAULT 0," +
                    "partidas_ganadas INTEGER DEFAULT 0," +
                    "mejor_puntuacion INTEGER DEFAULT 0," +
                    "tiempo_total INTEGER DEFAULT 0," +
                    "ultima_partida TEXT," +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                    ");";

            String sqlPartidas = "CREATE TABLE IF NOT EXISTS partidas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER NOT NULL," +
                    "fecha TEXT NOT NULL," +
                    "resultado TEXT NOT NULL," +
                    "intentos INTEGER NOT NULL," +
                    "palabra_jugada TEXT NOT NULL," +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                    ");";

            String sqlPalabras = "CREATE TABLE IF NOT EXISTS palabras (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "palabra TEXT UNIQUE NOT NULL," +
                    "veces_usada INTEGER DEFAULT 0," +
                    "veces_acertada INTEGER DEFAULT 0," +
                    "fecha_agregada TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ");";


            stmt.execute(sqlUsuarios);
            stmt.execute(sqlEstadisticas);
            stmt.execute(sqlPartidas);
            stmt.execute(sqlPalabras);

            System.out.println("Tablas inicializadas correctamente.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

