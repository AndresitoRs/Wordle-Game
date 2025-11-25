package di.wordle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class UsuarioManager {

    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private List<Usuario> usuarios;
    private Gson gson = new Gson();

    public UsuarioManager() {
        usuarios = cargarUsuarios();
    }

    private List<Usuario> cargarUsuarios() {
        try (Reader reader = new InputStreamReader(new FileInputStream(ARCHIVO_USUARIOS), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<Usuario>>(){}.getType();
            List<Usuario> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void guardarUsuarios() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(ARCHIVO_USUARIOS), StandardCharsets.UTF_8)) {
            gson.toJson(usuarios, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registrarUsuario(String usuario, String password) {
        if (buscarUsuario(usuario) != null) {
            return false; // Usuario ya existe
        }
        String hash = hashPassword(password);
        usuarios.add(new Usuario(usuario, hash));
        guardarUsuarios();
        return true;
    }

    public boolean autenticarUsuario(String usuario, String password) {
        Usuario u = buscarUsuario(usuario);
        if (u == null) return false;
        String hash = hashPassword(password);
        return u.getPasswordHash().equals(hash);
    }

    private Usuario buscarUsuario(String usuario) {
        return usuarios.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(usuario))
                .findFirst()
                .orElse(null);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
