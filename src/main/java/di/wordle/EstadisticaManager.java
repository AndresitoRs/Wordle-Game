package di.wordle;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

public class EstadisticaManager {

    public void actualizarEstadisticasEnMongo(int usuarioId, boolean gano, int puntos, int tiempoJugado) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> estadisticas = db.getCollection("estadisticas");

        // Buscar el documento actual para este usuario
        Document actual = estadisticas.find(Filters.eq("usuario_id", usuarioId)).first();

        int partidasJugadas = 1;
        int partidasGanadas = gano ? 1 : 0;

        if (actual != null) {
            // Leer valores actuales y sumarlos
            partidasJugadas += actual.getInteger("partidas_jugadas", 0);
            partidasGanadas += actual.getInteger("partidas_ganadas", 0);
            puntos += actual.getInteger("puntos", 0);
            tiempoJugado += actual.getInteger("tiempo_jugado", 0);

            // Actualizar el documento
            estadisticas.updateOne(
                    Filters.eq("usuario_id", usuarioId),
                    Updates.combine(
                            Updates.set("partidas_jugadas", partidasJugadas),
                            Updates.set("partidas_ganadas", partidasGanadas),
                            Updates.set("puntos", puntos),
                            Updates.set("tiempo_jugado", tiempoJugado)
                    )
            );
            System.out.println("Estadísticas actualizadas en MongoDB.");
        } else {
            // Si no existe, crea un nuevo documento
            Document nuevo = new Document()
                    .append("usuario_id", usuarioId)
                    .append("partidas_jugadas", partidasJugadas)
                    .append("partidas_ganadas", partidasGanadas)
                    .append("puntos", puntos)
                    .append("tiempo_jugado", tiempoJugado);
            estadisticas.insertOne(nuevo);
            System.out.println("Estadísticas creadas en MongoDB.");
        }
    }

    public Document obtenerEstadisticas(int usuarioId) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> estadisticas = db.getCollection("estadisticas");

        Document doc = estadisticas.find(Filters.eq("usuario_id", usuarioId)).first();
        if (doc == null) {
            System.out.println("No se encontró ningún documento para usuarioId: " + usuarioId);
            return new Document()
                    .append("partidas_jugadas", 0)
                    .append("partidas_ganadas", 0)
                    .append("puntos", 0)
                    .append("tiempo_jugado", 0);
        }

        return doc;
    }
}
