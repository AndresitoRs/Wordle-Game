package di.wordle;

public class Sesion {
    private static Sesion instancia = null;
    private String usuario;
    private long tiempoSegundos = 0;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void incrementarTiempoSegundos() {
        tiempoSegundos++;
    }

    public long getTiempoSegundos() {
        return tiempoSegundos;
    }

    public void resetTiempoSesion() {
        tiempoSegundos = 0;
    }
}
