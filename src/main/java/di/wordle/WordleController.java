package di.wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class WordleController implements Initializable {

    @FXML
    private AnchorPane fondo;
    @FXML
    private Button bplay;
    @FXML
    private Button bexit;
    @FXML
    private Button benviar;
    @FXML
    private Button borrar;

    @FXML
    Resultado info;
    private String palabraOculta;
    private int filaActual = 1;
    private Label casillaSeleccionada;
    @FXML
    private GridPane tablero;
    @FXML
    Label i1l1, i1l2, i1l3, i1l4, i1l5, i2l1, i2l2, i2l3, i2l4, i2l5, i3l1, i3l2, i3l3, i3l4, i3l5, i4l1, i4l2, i4l3, i4l4, i4l5, i5l1, i5l2, i5l3, i5l4, i5l5;

    @FXML
    public void iniciarPartida() {

        i1l1.setText("");
        i1l2.setText("");
        i1l3.setText("");
        i1l4.setText("");
        i1l5.setText("");
        i1l1.setOnMouseClicked(this::seleccionar);
        i1l2.setOnMouseClicked(this::seleccionar);
        i1l3.setOnMouseClicked(this::seleccionar);
        i1l4.setOnMouseClicked(this::seleccionar);
        i1l5.setOnMouseClicked(this::seleccionar);

        i2l1.setText("");
        i2l2.setText("");
        i2l3.setText("");
        i2l4.setText("");
        i2l5.setText("");
        i2l1.setOnMouseClicked(this::seleccionar);
        i2l2.setOnMouseClicked(this::seleccionar);
        i2l3.setOnMouseClicked(this::seleccionar);
        i2l4.setOnMouseClicked(this::seleccionar);
        i2l5.setOnMouseClicked(this::seleccionar);

        i3l1.setText("");
        i3l2.setText("");
        i3l3.setText("");
        i3l4.setText("");
        i3l5.setText("");
        i3l1.setOnMouseClicked(this::seleccionar);
        i3l2.setOnMouseClicked(this::seleccionar);
        i3l3.setOnMouseClicked(this::seleccionar);
        i3l4.setOnMouseClicked(this::seleccionar);
        i3l5.setOnMouseClicked(this::seleccionar);

        i4l1.setText("");
        i4l2.setText("");
        i4l3.setText("");
        i4l4.setText("");
        i4l5.setText("");
        i4l1.setOnMouseClicked(this::seleccionar);
        i4l2.setOnMouseClicked(this::seleccionar);
        i4l3.setOnMouseClicked(this::seleccionar);
        i4l4.setOnMouseClicked(this::seleccionar);
        i4l5.setOnMouseClicked(this::seleccionar);


        i5l1.setText("");
        i5l2.setText("");
        i5l3.setText("");
        i5l4.setText("");
        i5l5.setText("");
        i5l1.setOnMouseClicked(this::seleccionar);
        i5l2.setOnMouseClicked(this::seleccionar);
        i5l3.setOnMouseClicked(this::seleccionar);
        i5l4.setOnMouseClicked(this::seleccionar);
        i5l5.setOnMouseClicked(this::seleccionar);
    }

    public void mostrarBotonesFinal() {
        bplay.setVisible(true);
        bexit.setVisible(true);
    }

    public void volverMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApp.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) fondo.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        casillaSeleccionada = i1l1;
        casillaSeleccionada.getStyleClass().add("activa");
        palabraOculta = obtenerPalabraAleatoria();
        System.out.println(palabraOculta);
        iniciarPartida();
        bplay.setVisible(false);
        bexit.setVisible(false);
        URL ruta = getClass().getResource("img/fondo.png");
        String estilo = "-fx-background-image:url('"+ruta+"')";
        fondo.setStyle(estilo);
    }

    private String obtenerPalabraAleatoria() {
        try (InputStream is = getClass().getResourceAsStream("/palabras.txt")) {
            if (is == null) {
                System.out.println("No se pudo encontrar palabras.txt dentro del JAR");
                return "ERROR";
            }
            // Leer contenido
            String contenido = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Separar palabras
            String[] palabras = contenido.split("\\s+");
            List<String> palabrasFiltradas = Arrays.stream(palabras)
                    .filter(p -> p.length() == 5)
                    .collect(Collectors.toList());

            if (palabrasFiltradas.isEmpty()) return "ERROR";

            // Elegir palabra aleatoria
            Random random = new Random();
            return palabrasFiltradas.get(random.nextInt(palabrasFiltradas.size()));

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public void desmarcarTodas() {

        i1l1.getStyleClass().remove("activa");
        i1l2.getStyleClass().remove("activa");
        i1l3.getStyleClass().remove("activa");
        i1l4.getStyleClass().remove("activa");
        i1l5.getStyleClass().remove("activa");

        i2l1.getStyleClass().remove("activa");
        i2l2.getStyleClass().remove("activa");
        i2l3.getStyleClass().remove("activa");
        i2l4.getStyleClass().remove("activa");
        i2l5.getStyleClass().remove("activa");

        i3l1.getStyleClass().remove("activa");
        i3l2.getStyleClass().remove("activa");
        i3l3.getStyleClass().remove("activa");
        i3l4.getStyleClass().remove("activa");
        i3l5.getStyleClass().remove("activa");

        i4l1.getStyleClass().remove("activa");
        i4l2.getStyleClass().remove("activa");
        i4l3.getStyleClass().remove("activa");
        i4l4.getStyleClass().remove("activa");
        i4l5.getStyleClass().remove("activa");

        i5l1.getStyleClass().remove("activa");
        i5l2.getStyleClass().remove("activa");
        i5l3.getStyleClass().remove("activa");
        i5l4.getStyleClass().remove("activa");
        i5l5.getStyleClass().remove("activa");
    }

    public void seleccionar(MouseEvent event) {
        casillaSeleccionada = (Label) event.getSource();
        desmarcarTodas();
        casillaSeleccionada.getStyleClass().add("activa");
    }

    @FXML
    public void introducirLetra(ActionEvent event) {
        Button botonPulsado = (Button) event.getSource();
        if (casillaSeleccionada != null) {
            casillaSeleccionada.setText(botonPulsado.getText());
            String id = casillaSeleccionada.getId();
            int fila = Integer.parseInt(id.substring(1, 2)); // Extrae la fila
            int col = Integer.parseInt(id.substring(3)); // Extrae la columna
            col++;

            if (col > 5) {
                casillaSeleccionada.getStyleClass().remove("activa");
                casillaSeleccionada = null;
                return;
            }
            String nuevoId = "i" + fila + "l" + col;

            for (Node node : tablero.getChildren()) {
                if (node instanceof Label && node.getId().equals(nuevoId)) {
                    casillaSeleccionada.getStyleClass().remove("activa");
                    casillaSeleccionada = (Label) node;
                    casillaSeleccionada.getStyleClass().add("activa");
                    break;
                }
            }
        }
    }

    @FXML
    public void borrar(ActionEvent event) {
        if (casillaSeleccionada != null) {
            casillaSeleccionada.setText("");
            String id = casillaSeleccionada.getId();
            int fila = Integer.parseInt(id.substring(1, 2));
            int col = Integer.parseInt(id.substring(3));
            col--;
            if (col < 1) {
                col = 1;
            }
            if (fila >= 1 && col >= 1) {
                String nuevoId = "i" + fila + "l" + col;
                for (Node node : tablero.getChildren()) {
                    if (node instanceof Label && node.getId().equals(nuevoId)) {
                        // Remueve la clase 'activa' de la casilla seleccionada actual
                        casillaSeleccionada.getStyleClass().remove("activa");
                        casillaSeleccionada = (Label) node;
                        casillaSeleccionada.getStyleClass().add("activa");
                        break;
                    }
                }
            }
        }
    }
   /* public String[] comprobarPalabra(String intento, String secreta) {
        String[] colores = new String[intento.length()];

        for (int i = 0; i < intento.length(); i++) {
            char letra = intento.charAt(i);

            if (letra == secreta.charAt(i)) {
                colores[i] = "verde"; // letra correcta y en posición correcta
            } else if (secreta.contains(String.valueOf(letra))) {
                colores[i] = "amarillo"; // letra existe, pero en otra posición
            } else {
                colores[i] = "gris"; // letra no existe
            }
        }

        return colores;
    }
    */

    public void salir(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
