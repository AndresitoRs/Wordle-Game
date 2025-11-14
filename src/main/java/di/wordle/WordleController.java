package di.wordle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    private void procesarTeclaFisica(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.ENTER) {
            comprobarPalabra(null);
            event.consume();
        } else if (code == KeyCode.BACK_SPACE) {
            borrar(null);
            event.consume();
        } else if (code.isLetterKey()) {
            // Solo letras, sin incluir Enter ni otros símbolos
            String letra = event.getText().toUpperCase();

            // Asegurarse que letra sea una sola letra A-Z
            if (letra.matches("[A-Z]")) {
                agregarLetraAlTablero(letra);
                event.consume();
            }
        }
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
        info.limpiar();
        URL ruta = getClass().getResource("img/fondo.png");
        String estilo = "-fx-background-image:url('" + ruta + "')";
        fondo.setStyle(estilo);

        fondo.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    System.out.println("Tecla presionada: code=" + event.getCode() + ", texto='" + event.getText() + "'");
                    KeyCode code = event.getCode();
                    if (code == KeyCode.ENTER) {
                        comprobarPalabra(null);
                        event.consume();
                    } else if (code == KeyCode.BACK_SPACE) {
                        borrar(null);
                        event.consume();
                    } else if (code.isLetterKey()) {
                        String letra = event.getText().toUpperCase();
                        if (letra.isEmpty()) {
                            letra = code.getName().toUpperCase();
                        }
                        if (letra.length() == 1 && letra.matches("[A-Z]")) {
                            agregarLetraAlTablero(letra);
                            event.consume();
                        }
                    }
                });
            }
        });
        Platform.runLater(() -> {
            fondo.requestFocus();
        });
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

    @FXML
    public void comprobarPalabra(ActionEvent event) {
        // Verifica si todas las casillas de la fila actual están completas
        boolean filaCompleta = true;
        for (int col = 1; col <= 5; col++) {
            String casillaId = "i" + filaActual + "l" + col;
            Label casilla = (Label) tablero.lookup("#" + casillaId);
            if (casilla.getText().isEmpty()) {
                filaCompleta = false;
                break;
            }
        }

        // Si la fila actual no está completa, muestra un mensaje de advertencia
        if (!filaCompleta) {
            Alert alertaInfo = new Alert(Alert.AlertType.INFORMATION);
            alertaInfo.setTitle("Ojo");
            alertaInfo.setHeaderText("Cuidado");
            alertaInfo.setContentText("Sus filas no están completadas, o esta usted haciendo trampas, así que por favor, " +
                    "completa todas las casillas de la fila actual antes de comprobar la palabra o deje de intentar hacer trampa.");
            alertaInfo.showAndWait();
            return; // Salimos del método ya que no se cumplen los requisitos
        }

        StringBuilder palabraIntroducida = new StringBuilder();
        for (int col = 1; col <= 5; col++) {
            String casillaId = "i" + filaActual + "l" + col;
            Label casilla = (Label) tablero.lookup("#" + casillaId);
            palabraIntroducida.append(casilla.getText());
        }

        String palabraOculta = this.palabraOculta.toUpperCase();
        String palabraUsuario = palabraIntroducida.toString().toUpperCase();
        boolean adivinoPalabra = true;

        // Array para llevar un seguimiento de las letras de la palabra oculta que ya han sido usadas
        boolean[] letrasUsadas = new boolean[5];

        for (int col = 0; col < 5; col++) {
            String casillaId = "i" + filaActual + "l" + (col + 1);
            Label casilla = (Label) tablero.lookup("#" + casillaId);
            char letraUsuario = palabraUsuario.charAt(col);
            char letraOculta = palabraOculta.charAt(col);

            if (letraUsuario == letraOculta) {
                // Si la letra coincide exactamente en la posición correcta, cambia a "correcta"
                casilla.getStyleClass().removeAll("normal", "existe");
                casilla.getStyleClass().add("correcta");
                letrasUsadas[col] = true; // Marca la letra como usada
            } else {
                adivinoPalabra = false;
            }
        }

        // Segundo pase: marcar letras existentes pero no en la posición correcta
        for (int col = 0; col < 5; col++) {
            String casillaId = "i" + filaActual + "l" + (col + 1);
            Label casilla = (Label) tablero.lookup("#" + casillaId);
            char letraUsuario = palabraUsuario.charAt(col);

            if (!casilla.getStyleClass().contains("correcta")) {
                boolean letraEncontrada = false;
                for (int i = 0; i < 5; i++) {
                    if (!letrasUsadas[i] && palabraOculta.charAt(i) == letraUsuario) {
                        letraEncontrada = true;
                        letrasUsadas[i] = true; // Marca la letra como usada
                        break;
                    }
                }

                if (letraEncontrada) {
                    casilla.getStyleClass().removeAll("normal", "correcta");
                    casilla.getStyleClass().add("existe");
                } else {
                    casilla.getStyleClass().removeAll("existe", "correcta");
                    casilla.getStyleClass().add("normal");
                }
            }
        }

        if (adivinoPalabra) {
            info.ganar(); // Llama al método ganar si el usuario ha adivinado la palabra
            Alert alertaGanar = new Alert(Alert.AlertType.INFORMATION);
            alertaGanar.setTitle("Felicidades");
            alertaGanar.setHeaderText("Victoria");
            alertaGanar.setContentText("Buen trabajo, le he visto fino");
            alertaGanar.showAndWait();
            desactivarTodasLasCasillas(); // Desactiva todas las casillas si se adivina la palabra
            mostrarBotonesFinal();
        } else if (filaActual == 5) {
            Alert alertaPerder = new Alert(Alert.AlertType.ERROR);
            alertaPerder.setTitle("Una pena");
            alertaPerder.setHeaderText("Derrota");
            alertaPerder.setContentText("Ha perdido tras gastar sus intentos, la palabra era: " +palabraOculta);
            alertaPerder.showAndWait();
            // Si es el último intento y la palabra no es adivinada, desactiva las casillas de la última fila y llama a perder
            desactivarCasillasFila(filaActual);
            info.perder();
            mostrarBotonesFinal();
        } else {
            filaActual++;
            if (filaActual <= 5) {
                String nuevoId = "i" + filaActual + "l1";
                Node siguienteCasilla = tablero.lookup("#" + nuevoId);
                if (siguienteCasilla instanceof Label) {
                    if (casillaSeleccionada != null) {
                        casillaSeleccionada.getStyleClass().remove("activa");
                    }
                    casillaSeleccionada = (Label) siguienteCasilla;
                    casillaSeleccionada.getStyleClass().add("activa");
                }
            }
        }
    }

    private void desactivarCasillasFila(int fila) {
        for (int col = 1; col <= 5; col++) {
            String casillaId = "i" + fila + "l" + col;
            Label casilla = (Label) tablero.lookup("#" + casillaId);
            casilla.setDisable(true);
        }
    }

    private void desactivarTodasLasCasillas() {
        for (int fila = 1; fila <= 5; fila++) {
            desactivarCasillasFila(fila);
        }
    }

    @FXML
    private void reiniciarPartida() {
        // Restablece todas las casillas a su estado inicial
        for (int fila = 1; fila <= 5; fila++) {
            for (int col = 1; col <= 5; col++) {
                String casillaId = "i" + fila + "l" + col;
                Label casilla = (Label) tablero.lookup("#" + casillaId);
                casilla.setText("");
                casilla.getStyleClass().removeAll("activa", "correcta", "existe", "normal");
                casilla.setDisable(false); // Habilita todas las casillas
            }
        }
        casillaSeleccionada = (Label) tablero.lookup("#i1l1");
        casillaSeleccionada.getStyleClass().add("activa");
        filaActual = 1;
        palabraOculta = obtenerPalabraAleatoria();
        System.out.println(palabraOculta);
        iniciarPartida();
        info.limpiar();
        bplay.setVisible(false);
        bexit.setVisible(false);

        Scene escena = fondo.getScene();
        if (escena != null) {
            escena.setOnKeyPressed(event -> procesarTeclaFisica(event));
        }

        // Asegurar que fondo tiene el foco para recibir eventos de teclado
        fondo.requestFocus();
    }

    public void salir(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    /**
     * Muestra una alerta con un título y mensaje.
     */

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void agregarLetraAlTablero(String letra) {
        if (casillaSeleccionada == null) {
            // Si no hay casilla seleccionada, selecciona la primera vacía de la filaActual
            for (int col = 1; col <= 5; col++) {
                String id = "i" + filaActual + "l" + col;
                Node node = tablero.lookup("#" + id);
                if (node instanceof Label) {
                    Label lab = (Label) node;
                    if (lab.getText() == null || lab.getText().isEmpty()) {
                        if (casillaSeleccionada != null) casillaSeleccionada.getStyleClass().remove("activa");
                        casillaSeleccionada = lab;
                        casillaSeleccionada.getStyleClass().add("activa");
                        break;
                    }
                }
            }
        }

        if (casillaSeleccionada != null) {
            casillaSeleccionada.setText(letra);
            String id = casillaSeleccionada.getId();
            int fila = Integer.parseInt(id.substring(1, 2));
            int col = Integer.parseInt(id.substring(3));
            col++;

            if (col > 5) {
                // fin de fila
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
}
