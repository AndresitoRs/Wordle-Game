package di.wordle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.util.Duration;
import javafx.scene.control.Label;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import di.wordle.Sesion;


public class ControladorMenu implements Initializable {

    private HostServices hostServices;

    @FXML private AnchorPane fondo;
    @FXML private Button bjugar;
    @FXML private Button bsalir;
    @FXML private Button bgit;

    @FXML private ComboBox<String> comboIdioma;

    @FXML private Label lblUsuarioSesion;
    @FXML private Label lblTiempoSesion;

    private Timeline timeline;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (comboIdioma != null) {
            comboIdioma.getItems().clear();
            comboIdioma.getItems().addAll("🇪🇸 Español", "🇬🇧 English", "🇬🇦 Galego");
            comboIdioma.getSelectionModel().selectFirst();
        }

        // Mostrar usuario logueado y arrancar timer sesión
        String usuario = Sesion.getInstancia().getUsuario();
        System.out.println("Usuario recibido en menú: " + usuario);
        if (usuario != null && !usuario.isEmpty()) {
            lblUsuarioSesion.setText("Está identificado como: \"" + usuario + "\"");
        } else {
            lblUsuarioSesion.setText("No hay usuario identificado");
        }

        iniciarRefrescoVisual();
    }

    public void iniciarRefrescoVisual() {
        Timeline refresco = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long segundosTotales = Sesion.getInstancia().getTiempoSegundos();
            long minutos = segundosTotales / 60;
            long segundos = segundosTotales % 60;

            lblTiempoSesion.setText(
                    String.format("Tiempo de sesión: %02d:%02d", minutos, segundos)
            );
        }));

        refresco.setCycleCount(Timeline.INDEFINITE);
        refresco.play();
    }

    public void salir(ActionEvent event) {
        detenerTimerSesion();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cargarPantalla1() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApp.class.getResource("wordle.fxml"));
        Parent root = fxmlLoader.load();

        WordleController controladorWordle = fxmlLoader.getController();
        String idiomaSeleccionado = comboIdioma.getSelectionModel().getSelectedItem();
        controladorWordle.setIdiomaSeleccionado(idiomaSeleccionado);
        controladorWordle.iniciarConIdioma();

        Scene scene = new Scene(root);
        // CARGAR CSS IMPORTANTE
        scene.getStylesheets().add(WordleApp.class.getResource("estilos.css").toExternalForm());

        Stage stage = (Stage) fondo.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Wordle");
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

    private void detenerTimerSesion() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    // Efectos botones

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
