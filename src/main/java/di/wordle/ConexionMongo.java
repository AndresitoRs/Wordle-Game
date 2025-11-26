package di.wordle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ConexionMongo {

    private static final String URI = "mongodb+srv://andres:abc123.@cluster1.tqrnbor.mongodb.net/?retryWrites=true&w=majority";
    private static final String DB_NAME = "WordleDB";

    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    // Método para obtener la instancia única de MongoDatabase
    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(DB_NAME);
        }
        return database;
    }

    // Método para cerrar la conexión si alguna vez es necesario
    public static void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }

    // Método main para test rápido de conexión
    public static void main(String[] args) {
        try {
            MongoDatabase db = getDatabase();
            db.runCommand(new Document("ping", 1));
            System.out.println("Conexión a MongoDB exitosa!");
        } catch (Exception e) {
            System.err.println("Error conectando a MongoDB:");
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }
}
