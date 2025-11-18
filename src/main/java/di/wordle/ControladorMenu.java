package di.wordle;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControladorMenu {

    private HostServices hostServices;
    @FXML
    private AnchorPane fondo;
    @FXML
    private Button btnJugar;
    @FXML
    private Button btnSalir;
    @FXML
    private Button btnGit;


    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void salir(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cargarPantalla1() throws IOException {
        cargarPantalla("wordle.fxml");
    }

    @FXML
    public void cargarPantalla2() throws IOException {
        if (hostServices != null) {
            hostServices.showDocument("https://github.com/AndresitoRs/Wordle-Game");
        } else {
            System.out.println("HostServices no está disponible.");
        }
    }

    public void cargarPantalla(String pantalla) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApp.class.getResource(pantalla));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) fondo.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void zoomIn(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setScaleX(1.1);
        btn.setScaleY(1.1);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(6);            // Aumenta el radio para que la sombra sea más grande
        shadow.setOffsetX(2);           // Más desplazamiento horizontal
        shadow.setOffsetY(2);           // Más desplazamiento vertical
        shadow.setColor(Color.rgb(0, 0, 0, 0.85)); // Sombra más oscura y visible (85% opacidad)

        // Acceder al Label interno del Button y poner el efecto sólo en el texto
        Node textNode = btn.lookup(".text");
        if (textNode != null) {
            textNode.setEffect(shadow);
        }
    }

    @FXML
    public void zoomOut(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setScaleX(1.0);
        btn.setScaleY(1.0);

        Node textNode = btn.lookup(".text");
        if (textNode != null) {
            textNode.setEffect(null);
        }
    }

    @FXML
    public void brilloOn(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setEffect(new Glow(0.7));
    }

    @FXML
    public void brilloOff(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setEffect(null);
    }
}

