package di.wordle;

import di.wordle.db.Database;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.sql.*;
import java.util.Optional;

public class PalabraManager {

    public PalabraManager() {
        crearTablaSiNoExiste();
        agregarColumnasSiFaltan();
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS palabras (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "palabra TEXT UNIQUE NOT NULL," +
                "veces_usada INTEGER DEFAULT 0," +
                "veces_acertada INTEGER DEFAULT 0," +
                "fecha_agregada TEXT DEFAULT CURRENT_TIMESTAMP" +
                ");";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarColumnasSiFaltan() {
        try (Connection conn = Database.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            // veces_usada
            try (ResultSet rs = meta.getColumns(null, null, "palabras", "veces_usada")) {
                if (!rs.next()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE palabras ADD COLUMN veces_usada INTEGER DEFAULT 0;");
                        System.out.println("Añadida columna 'veces_usada'");
                    }
                }
            }

            // veces_acertada
            try (ResultSet rs = meta.getColumns(null, null, "palabras", "veces_acertada")) {
                if (!rs.next()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE palabras ADD COLUMN veces_acertada INTEGER DEFAULT 0;");
                        System.out.println("Añadida columna 'veces_acertada'");
                    }
                }
            }

            // fecha_agregada
            try (ResultSet rs = meta.getColumns(null, null, "palabras", "fecha_agregada")) {
                if (!rs.next()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE palabras ADD COLUMN fecha_agregada TEXT;");
                        System.out.println("Añadida columna 'fecha_agregada'");
                    }
                    // Rellena con la fecha actual las filas existentes
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("UPDATE palabras SET fecha_agregada = datetime('now') WHERE fecha_agregada IS NULL;");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertarPalabra(String palabra) {
        String sql = """
        INSERT OR IGNORE INTO palabras (palabra, fecha_agregada)
        VALUES (?, datetime('now'))
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, palabra.toLowerCase());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void incrementarVecesUsada(String palabra) {
        String sql = "UPDATE palabras SET veces_usada = veces_usada + 1 WHERE palabra = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, palabra.toLowerCase());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener datos palabra (opcional para otros usos)
    public Optional<Palabra> obtenerPalabra(String palabra) {
        String sql = "SELECT * FROM palabras WHERE palabra = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, palabra.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Palabra p = new Palabra(
                        rs.getInt("id"),
                        rs.getString("palabra"),
                        rs.getInt("veces_usada"),
                        rs.getInt("veces_acertada"),
                        rs.getString("fecha_agregada")
                );
                return Optional.of(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Para sincronizar en MongoDB
    public void sincronizarPalabraEnMongo(Palabra palabra) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<org.bson.Document> coleccionPalabras = db.getCollection("palabras");

        org.bson.Document doc = new org.bson.Document()
                .append("id", palabra.getId())
                .append("palabra", palabra.getPalabra())
                .append("veces_usada", palabra.getVecesUsada())
                .append("veces_acertada", palabra.getVecesAcertada())
                .append("fecha_agregada", palabra.getFechaAgregada());

        // Upsert: actualiza si existe, inserta si no
        coleccionPalabras.replaceOne(
                new org.bson.Document("palabra", palabra.getPalabra()),
                doc,
                new com.mongodb.client.model.ReplaceOptions().upsert(true)
        );
    }

    public void palabraAcertada(String palabra) {
        String sql = "UPDATE palabras SET veces_acertada = veces_acertada + 1 WHERE palabra = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);  // Desactivar autocommit para control manual
            stmt.setString(1, palabra.toLowerCase());
            int filasActualizadas = stmt.executeUpdate();
            conn.commit();  // Forzar commit de la transacción
            System.out.println("palabraAcertada: filas actualizadas = " + filasActualizadas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarVecesAcertada(String palabra, int veces) {
        String sql = "UPDATE palabras SET veces_acertada = ? WHERE palabra = ?";
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, veces);
                stmt.setString(2, palabra.toLowerCase());
                int filasActualizadas = stmt.executeUpdate();
                System.out.println("actualizarVecesAcertada: filas actualizadas = " + filasActualizadas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
