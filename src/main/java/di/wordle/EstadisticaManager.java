package di.wordle;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

public class EstadisticaManager {

    public void actualizarEstadisticasEnMongo(int usuarioId, int partidasJugadas, int partidasGanadas, int puntos, int tiempoJugado) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> estadisticas = db.getCollection("estadisticas");

        UpdateResult result = estadisticas.updateOne(
                Filters.eq("usuario_id", usuarioId),
                Updates.combine(
                        Updates.set("partidas_jugadas", partidasJugadas),
                        Updates.set("partidas_ganadas", partidasGanadas),
                        Updates.set("puntos", puntos),
                        Updates.set("tiempo_jugado", tiempoJugado)
                )
        );

        if (result.getMatchedCount() == 0) {
            Document nuevo = new Document()
                    .append("usuario_id", usuarioId)
                    .append("partidas_jugadas", partidasJugadas)
                    .append("partidas_ganadas", partidasGanadas)
                    .append("puntos", puntos)
                    .append("tiempo_jugado", tiempoJugado);
            estadisticas.insertOne(nuevo);
            System.out.println("Estadísticas creadas en MongoDB.");
        } else {
            System.out.println("Estadísticas actualizadas en MongoDB.");
        }
    }
}
