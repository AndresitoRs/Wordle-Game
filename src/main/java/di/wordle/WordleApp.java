package di.wordle;

import di.wordle.db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class WordleApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Database.inicializar();
        // Cargar FXML de forma segura
        URL fxml = Objects.requireNonNull(
                WordleApp.class.getResource("/di/wordle/login.fxml"),
                "No se pudo encontrar login.fxml"
        );

        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 550, 670);

        // Cargar el CSS de forma segura
        URL css = Objects.requireNonNull(
                WordleApp.class.getResource("/di/wordle/estilos.css"),
                "No se pudo encontrar estilos.css"
        );
        scene.getStylesheets().add(css.toExternalForm());

        // Pasar HostServices al controlador
        LoginController controlador = fxmlLoader.getController();
        controlador.setHostServices(getHostServices());

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
