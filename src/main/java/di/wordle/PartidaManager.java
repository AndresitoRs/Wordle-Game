package di.wordle;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class PartidaManager {

    public void guardarPartidaEnMongo(int id, int usuarioId, String resultado, int intentos, String palabra) {
        MongoDatabase db = ConexionMongo.getDatabase();
        MongoCollection<Document> partidas = db.getCollection("partidas");

        Document partidaDoc = new Document()
                .append("id", id)
                .append("usuario_id", usuarioId)
                .append("resultado", resultado)
                .append("intentos", intentos)
                .append("palabra", palabra);

        partidas.insertOne(partidaDoc);
        System.out.println("Partida guardada en MongoDB.");
    }
}
