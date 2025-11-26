package di.wordle.db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {

    public static void crearTablas() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabla usuarios
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);

            // Tabla estadísticas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS estadisticas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario_id INTEGER,
                    partidas INTEGER DEFAULT 0,
                    victorias INTEGER DEFAULT 0,
                    derrotas INTEGER DEFAULT 0,
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                )
            """);

            // Tabla palabras jugadas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS palabras (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    palabra TEXT NOT NULL,
                    veces_usada INTEGER DEFAULT 0
                )
            """);

            System.out.println("✅ Base de datos lista");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
