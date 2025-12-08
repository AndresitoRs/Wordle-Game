package di.wordle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Sesion {

    private static Sesion instancia = null;
    private String usuario;
    private long tiempoSegundos = 0;
    private Timeline timeline;
    private int usuarioId; // nuevo campo


    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void setUsuarioId(int id) { this.usuarioId = id; }

    public int getUsuarioId() { return usuarioId; }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public long getTiempoSegundos() {
        return tiempoSegundos;
    }

    public void resetTiempoSesion() {
        tiempoSegundos = 0;
    }

    public void iniciarTimerGlobal() {
        if (timeline != null) {
            return; // Ya está corriendo
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tiempoSegundos++;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void detenerTimer() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }
    public void cerrarSesion() {
        usuario = null;
        usuarioId = 0;
        tiempoSegundos = 0;
        detenerTimer(); // Detiene el contador de sesión
    }

}
