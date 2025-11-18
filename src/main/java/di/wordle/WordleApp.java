package di.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class WordleApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApp.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        ControladorMenu controlador = fxmlLoader.getController();
        WordleController controller = new WordleController(); controlador.setHostServices(getHostServices());
        stage.setTitle("Wordle"); stage.setScene(scene); stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}