package di.wordle;

import di.wordle.ControladorResultado;
import di.wordle.WordleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Resultado extends AnchorPane {

    private ControladorResultado controlador;

    public Resultado() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resultado.fxml"));
            controlador = new ControladorResultado();
            loader.setController(controlador);
            Node nodo = loader.load();
            this.getChildren().add(nodo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ganar() {
        Image img = new Image(getClass().getResourceAsStream("/di/wordle/img/tuganas.png"));
        controlador.getImagen().setImage(img);

    }

    public void perder() {
        Image img = new Image(getClass().getResourceAsStream("/di/wordle/img/juegoterminado.png"));
        controlador.getImagen().setImage(img);

    }
}