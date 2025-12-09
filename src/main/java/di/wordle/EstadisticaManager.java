package di.wordle;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstadisticaManager {

    // Manteniendo el mismo nombre que tu llamada
    public void actualizarEstadisticasEnMongo(int usuarioId, boolean ganoPartida, int puntosGanados, int tiempoJugadoSegundos) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> estadisticasMongo = db.getCollection("estadisticas");

        Document stats = estadisticasMongo.find(Filters.eq("usuario_id", usuarioId)).first();

        if (stats == null) {
            Document nuevo = new Document()
                    .append("usuario_id", usuarioId)
                    .append("partidas_jugadas", 1)
                    .append("partidas_ganadas", ganoPartida ? 1 : 0)
                    .append("mejor_puntuacion", puntosGanados)
                    .append("tiempo_total", tiempoJugadoSegundos)
                    .append("usuario", Sesion.getInstancia().getUsuario() != null ? Sesion.getInstancia().getUsuario() : "Desconocido");
            estadisticasMongo.insertOne(nuevo);
        } else {
            int actualesPartidas = getIntSafe(stats, "partidas_jugadas");
            int actualesGanadas = getIntSafe(stats, "partidas_ganadas");
            int actualesPuntos = getIntSafe(stats, "mejor_puntuacion");
            long actualesTiempo = getLongSafe(stats, "tiempo_total");

            Document update = new Document()
                    .append("$inc", new Document()
                            .append("partidas_jugadas", 1)
                            .append("partidas_ganadas", ganoPartida ? 1 : 0)
                            .append("mejor_puntuacion", puntosGanados)
                            .append("tiempo_total", tiempoJugadoSegundos)
                    )
                    .append("$set", new Document()
                            .append("usuario", Sesion.getInstancia().getUsuario() != null ? Sesion.getInstancia().getUsuario() : "Desconocido")
                    );

            estadisticasMongo.updateOne(Filters.eq("usuario_id", usuarioId), update);

        }
    }

    // Métodos auxiliares
    private int getIntSafe(Document doc, String key) {
        Number n = (Number) doc.get(key);
        return n != null ? n.intValue() : 0;
    }

    private long getLongSafe(Document doc, String key) {
        Number n = (Number) doc.get(key);
        return n != null ? n.longValue() : 0L;
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

    public List<Document> obtenerRankingGlobal() {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> col = db.getCollection("estadisticas");

        return col.find()
                .sort(Sorts.descending("puntos"))
                .limit(10)
                .into(new ArrayList<>());
    }

}
