package di.wordle;

public class Usuario {
    private String usuario;
    private String passwordHash;

    public Usuario(String usuario, String passwordHash) {
        this.usuario = usuario;
        this.passwordHash = passwordHash;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}