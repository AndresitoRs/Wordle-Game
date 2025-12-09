package di.wordle;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
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

        // Primero comprobamos si el usuario existe
        if (!usuarioManager.existeUsuario(usuario)) {
            mostrarAlerta("Error", "El usuario no existe. Debes registrarte primero.");
            return;
        }

        // Ahora comprobamos si la contraseña es correcta
        if (usuarioManager.autenticarUsuario(usuario, password)) {
            Sesion.getInstancia().setUsuario(usuario);
            Sesion.getInstancia().resetTiempoSesion();
            Sesion.getInstancia().iniciarTimerGlobal();

            // **AÑADIDO: sincronizar usuario en Mongo justo aquí**
            Long usuarioId = usuarioManager.obtenerUsuarioId(usuario);
            if (usuarioId != null) {
                Sesion.getInstancia().setUsuarioId(usuarioId.intValue()); // <-- guardar ID real en sesión
                usuarioManager.sincronizarUsuarioEnMongo(usuarioId.intValue(), usuario);
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
                Parent root = loader.load();

                // Pasar HostServices al controlador menú
                ControladorMenu menuController = loader.getController();
                menuController.setHostServices(hostServices);

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
        } else {
            mostrarAlerta("Error", "Contraseña incorrecta.");
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

    private String generarCaptcha(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }


    @FXML
    private void olvidarPassword() {
        // 1. Generar captcha aleatorio
        String captcha = generarCaptcha(5);

        TextInputDialog dialogCaptcha = new TextInputDialog();
        dialogCaptcha.setTitle("Verificación");
        dialogCaptcha.setHeaderText("Recuperación de contraseña");
        dialogCaptcha.setContentText("Introduce el siguiente código: " + captcha);

        var resultadoCaptcha = dialogCaptcha.showAndWait();
        if (resultadoCaptcha.isEmpty() || !resultadoCaptcha.get().equals(captcha)) {
            mostrarAlerta("Error", "Código incorrecto.");
            return;
        }

        // 2. Pedir usuario
        TextInputDialog dialogUsuario = new TextInputDialog();
        dialogUsuario.setTitle("Usuario");
        dialogUsuario.setHeaderText("Recuperación de contraseña");
        dialogUsuario.setContentText("Introduce tu nombre de usuario:");

        var resultadoUsuario = dialogUsuario.showAndWait();
        if (resultadoUsuario.isEmpty()) return;

        String usuario = resultadoUsuario.get();

        if (!usuarioManager.existeUsuario(usuario)) {
            mostrarAlerta("Error", "El usuario no existe.");
            return;
        }

        // 3. Pedir nueva contraseña
        TextInputDialog dialogNuevaPass = new TextInputDialog();
        dialogNuevaPass.setTitle("Nueva contraseña");
        dialogNuevaPass.setHeaderText("Restablecer contraseña");
        dialogNuevaPass.setContentText("Introduce la nueva contraseña:");

        var resultadoPass = dialogNuevaPass.showAndWait();
        if (resultadoPass.isEmpty()) {
            mostrarAlerta("Error", "La contraseña no puede estar vacía.");
            return;
        }

        String nuevaPassword = resultadoPass.get().trim();

        // 4. Guardar nuevo hash en la BD
        if (usuarioManager.actualizarPassword(usuario, nuevaPassword)) {
            mostrarAlerta("Éxito", "La contraseña se ha restablecido correctamente.");
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la contraseña.");
        }
    }


}
