package di.wordle;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import di.wordle.db.Database;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class UsuarioManager {

    public UsuarioManager() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL" +
                ");";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sincronizarUsuarioEnMongo(long usuarioId, String nombre) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> usuarios = db.getCollection("usuarios");
        MongoCollection<Document> estadisticasMongo = db.getCollection("estadisticas");

        // Limpiar posibles documentos nulos
        estadisticasMongo.deleteMany(Filters.eq("usuario", null));
        usuarios.deleteMany(Filters.eq("nombre", null));

        // Buscar si ya existe el usuario en mongo:
        Document usuarioMongo = usuarios.find(Filters.eq("id", usuarioId)).first();

        if (usuarioMongo == null) {
            // No existe, lo creamos
            Document nuevoUsuario = new Document()
                    .append("id", usuarioId)
                    .append("nombre", nombre);
            usuarios.insertOne(nuevoUsuario);
            System.out.println("Usuario creado en MongoDB");

            // Crear estadísticas solo si no existen
            try (Connection conn = Database.getConnection()) {
                var stmt = conn.prepareStatement(
                        "SELECT partidas_jugadas, partidas_ganadas, mejor_puntuacion, tiempo_total FROM estadisticas WHERE usuario_id = ?");
                stmt.setLong(1, usuarioId);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    Document estadisticasDoc = new Document()
                            .append("usuario_id", usuarioId)
                            .append("usuario", nombre)
                            .append("partidas_jugadas", rs.getInt("partidas_jugadas"))
                            .append("partidas_ganadas", rs.getInt("partidas_ganadas"))
                            .append("mejor_puntuacion", rs.getInt("mejor_puntuacion"))
                            .append("tiempo_total", rs.getLong("tiempo_total"));
                    estadisticasMongo.insertOne(estadisticasDoc);
                    System.out.println("Estadísticas creadas en MongoDB");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Usuario ya existe, no tocar estadísticas
            System.out.println("Usuario ya existe en MongoDB, se mantienen sus estadísticas");
        }
    }




    public boolean registrarUsuario(String nombre, String password) {
        String hash = hashPassword(password);
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            var stmtUser = conn.prepareStatement(
                    "INSERT INTO usuarios (nombre, password) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmtUser.setString(1, nombre);
            stmtUser.setString(2, hash);
            int affectedRows = stmtUser.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            ResultSet generatedKeys = stmtUser.getGeneratedKeys();
            if (generatedKeys.next()) {
                long usuarioId = generatedKeys.getLong(1);

                var stmtStats = conn.prepareStatement(
                        "INSERT INTO estadisticas (usuario_id, partidas_jugadas, partidas_ganadas, mejor_puntuacion, tiempo_total, ultima_partida) " +
                                "VALUES (?, 0, 0, 0, 0, NULL)"
                );
                stmtStats.setLong(1, usuarioId);
                stmtStats.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean autenticarUsuario(String nombre, String password) {
        String sql = "SELECT password FROM usuarios WHERE nombre = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashEnBD = rs.getString("password");
                String hashPassword = hashPassword(password);
                return hashEnBD.equals(hashPassword);
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeUsuario(String nombre) {
        String sql = "SELECT 1 FROM usuarios WHERE nombre = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertarPartida(long usuarioId, String resultado, int intentos, String palabraJugado) {
        String fecha = java.time.LocalDateTime.now().toString();

        try (Connection conn = Database.getConnection()) {
            var stmt = conn.prepareStatement(
                    "INSERT INTO partidas (usuario_id, fecha, resultado, intentos, palabra_jugada) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setLong(1, usuarioId);
            stmt.setString(2, fecha);
            stmt.setString(3, resultado);
            stmt.setInt(4, intentos);
            stmt.setString(5, palabraJugado);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarEstadisticas(long usuarioId, boolean gano, int puntosObtenidos, long tiempoPartidaSegundos) {
        try (Connection conn = Database.getConnection()) {
            var stmtSelect = conn.prepareStatement(
                    "SELECT partidas_jugadas, partidas_ganadas, mejor_puntuacion, tiempo_total FROM estadisticas WHERE usuario_id = ?"
            );
            stmtSelect.setLong(1, usuarioId);
            ResultSet rs = stmtSelect.executeQuery();

            if (rs.next()) {
                int partidasJugadas = rs.getInt("partidas_jugadas") + 1;
                int partidasGanadas = rs.getInt("partidas_ganadas") + (gano ? 1 : 0);
                int mejorPuntuacion = Math.max(rs.getInt("mejor_puntuacion"), puntosObtenidos);
                long tiempoTotal = rs.getLong("tiempo_total") + tiempoPartidaSegundos;

                var stmtUpdate = conn.prepareStatement(
                        "UPDATE estadisticas SET partidas_jugadas = ?, partidas_ganadas = ?, mejor_puntuacion = ?, tiempo_total = ?, ultima_partida = ? WHERE usuario_id = ?"
                );
                stmtUpdate.setInt(1, partidasJugadas);
                stmtUpdate.setInt(2, partidasGanadas);
                stmtUpdate.setInt(3, mejorPuntuacion);
                stmtUpdate.setLong(4, tiempoTotal);
                stmtUpdate.setString(5, java.time.LocalDateTime.now().toString());
                stmtUpdate.setLong(6, usuarioId);

                stmtUpdate.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long obtenerUsuarioId(String nombre) {
        try (Connection conn = Database.getConnection()) {
            var stmt = conn.prepareStatement("SELECT id FROM usuarios WHERE nombre = ?");
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean actualizarPassword(String nombre, String nuevaPassword) {
        String nuevoHash = hashPassword(nuevaPassword);

        String sql = "UPDATE usuarios SET password = ? WHERE nombre = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoHash);
            stmt.setString(2, nombre);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void guardarUsuarioEnMongo(int id, String nombre, String password) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> usuarios = db.getCollection("usuarios");

        Document usuarioDoc = new Document()
                .append("id", id)
                .append("nombre", nombre)
                .append("password", password);

        usuarios.insertOne(usuarioDoc);
        System.out.println("Usuario guardado en MongoDB.");
    }
}
