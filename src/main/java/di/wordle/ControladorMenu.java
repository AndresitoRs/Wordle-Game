package di.wordle;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private Button bjugar;
    @FXML
    private Button bsalir;
    @FXML
    private Button bgit;

    @FXML
    private ComboBox<String> comboIdioma;  // Añade esto (debe coincidir con fx:id en fxml)

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Rellenar combo con opciones, si no está hecho en fxml
        if (comboIdioma != null) {
            comboIdioma.getItems().clear();
            comboIdioma.getItems().addAll("🇪🇸 Español", "🇬🇧 English", "🇬🇦 Galego");
            comboIdioma.getSelectionModel().selectFirst();  // Seleccionar Español por defecto
        }
    }

    public void salir(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cargarPantalla1() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApp.class.getResource("wordle.fxml"));
        Parent root = fxmlLoader.load();
        // Obtener el controlador del Wordle para pasar el idioma seleccionado
        WordleController controladorWordle = fxmlLoader.getController();
        String idiomaSeleccionado = comboIdioma.getSelectionModel().getSelectedItem();
        controladorWordle.setIdiomaSeleccionado(idiomaSeleccionado);
        controladorWordle.iniciarConIdioma();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());

        Stage stage = (Stage) fondo.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void cargarPantalla2() throws IOException {
        if (hostServices != null) {
            hostServices.showDocument("https://github.com/AndresitoRs/Wordle-Game");
        } else {
            System.out.println("HostServices no está disponible.");
        }
    }

    @FXML
    public void zoomIn(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setScaleX(1.1);
        btn.setScaleY(1.1);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(6);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.85));

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
