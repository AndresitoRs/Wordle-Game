package di.wordle;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
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
import java.util.List;
import java.util.ResourceBundle;
import di.wordle.Sesion;
import org.bson.Document;


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
        System.out.println("Usuario ID en sesión: " + Sesion.getInstancia().getUsuarioId());
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
            hostServices.showDocument("https://github.com/AndresitoRs/Wordle-Game/tree/master");
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

    @FXML
    public void mostrarEstadisticas() {
        Task<Document> task = new Task<>() {
            @Override
            protected Document call() {
                int usuarioId = Sesion.getInstancia().getUsuarioId();
                EstadisticaManager manager = new EstadisticaManager();
                return manager.obtenerEstadisticas(usuarioId);
            }
        };

        task.setOnSucceeded(e -> {
            Document stats = task.getValue();

            Stage popup = new Stage();
            popup.setTitle("Estadísticas del Juego");

            VBox layout = new VBox(20);
            layout.setStyle(
                    "-fx-padding: 25;" +
                            "-fx-background-color: linear-gradient(to bottom, #1a1a1a, #333333);" +
                            "-fx-alignment: center;"
            );

            Label titulo = new Label("Tus Estadísticas");
            titulo.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 24; -fx-font-weight: bold;");

            int partidasJugadas = stats.getInteger("partidas_jugadas", 0);
            int partidasGanadas = stats.getInteger("partidas_ganadas", 0);
            int puntos = stats.getInteger("puntos", 0);
            int tiempo = stats.getInteger("tiempo_jugado", 0);

            Label lblPartidas = new Label("Partidas jugadas: " + partidasJugadas);
            Label lblGanadas = new Label("Partidas ganadas: " + partidasGanadas);
            Label lblPuntos = new Label("Puntos: " + puntos);
            Label lblTiempo = new Label("Tiempo jugado: " + tiempo + " s");

            lblPartidas.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16;");
            lblGanadas.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 16;");
            lblPuntos.setStyle("-fx-text-fill: #FF69B4; -fx-font-size: 16;");
            lblTiempo.setStyle("-fx-text-fill: #00BFFF; -fx-font-size: 16;");

            // Barras de progreso
            ProgressBar pbGanadas = new ProgressBar();
            pbGanadas.setPrefWidth(250);
            pbGanadas.setProgress(partidasJugadas == 0 ? 0 : (double) partidasGanadas / partidasJugadas);
            pbGanadas.setStyle("-fx-accent: #00FF00;");

            ProgressBar pbPuntos = new ProgressBar();
            pbPuntos.setPrefWidth(250);
            pbPuntos.setProgress(Math.min(1.0, puntos / 500.0)); // Normalizamos para barra
            pbPuntos.setStyle("-fx-accent: #FF69B4;");

            // Botón cerrar
            Button cerrar = new Button("Cerrar");
            cerrar.setStyle(
                    "-fx-font-size: 16;" +
                            "-fx-background-color: #FFD700;" +
                            "-fx-text-fill: #1a1a1a;" +
                            "-fx-font-weight: bold;"
            );
            cerrar.setOnAction(ev -> popup.close());

            layout.getChildren().addAll(
                    titulo,
                    lblPartidas, pbGanadas,
                    lblGanadas,
                    lblPuntos, pbPuntos,
                    lblTiempo,
                    cerrar
            );

            Scene scene = new Scene(layout);
            popup.setScene(scene);
            popup.initOwner(fondo.getScene().getWindow());
            popup.show();
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }

}
