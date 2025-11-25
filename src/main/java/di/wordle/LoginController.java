package di.wordle;
import di.wordle.Sesion;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    private HostServices hostServices;
    @FXML
    private PasswordField txtPassword;

    private UsuarioManager usuarioManager = new UsuarioManager();

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }


    @FXML
    private void login(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Debes rellenar usuario y contraseña.");
            return;
        }
// Dentro de LoginController.login()
        if (usuarioManager.autenticarUsuario(usuario, password)) {
            Sesion.getInstancia().setUsuario(usuario);
            Sesion.getInstancia().resetTiempoSesion();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
                Parent root = loader.load();

                // Pasar HostServices al controlador menú
                ControladorMenu menuController = loader.getController();
                menuController.setHostServices(hostServices);

                // Pero HostServices no está en LoginController, así que:
                // Necesitarás que LoginController tenga un método setHostServices() y que WordleApp se lo pase.

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(WordleApp.class.getResource("estilos.css").toExternalForm());

                stage.setScene(scene);
                stage.setTitle("Menú principal");
                stage.show();

                ((Node) event.getSource()).getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir la ventana del menú.");
            }
        }
    }


    @FXML
    private void registrar() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Debes rellenar usuario y contraseña.");
            return;
        }

        if (usuarioManager.registrarUsuario(usuario, password)) {
            mostrarAlerta("Éxito", "Usuario registrado correctamente. Ya puedes hacer login.");
        } else {
            mostrarAlerta("Error", "El usuario ya existe.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
