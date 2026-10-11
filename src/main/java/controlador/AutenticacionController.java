package controlador;

import modelo.Usuario;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AutenticacionController {

    public Usuario iniciarSesion(String rut, String contrasena) {

        if (rut == null || rut.isBlank()
                || contrasena == null || contrasena.isEmpty()) {
            return null;
        }

        String sql = """
                SELECT id, nombre, rut, correo, `contraseña`, rol
                FROM usuarios
                WHERE rut = ? AND `contraseña` = ?
                """;

        try (Connection conn =
                     DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut.trim());
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("correo"),
                            null,
                            rs.getString("rol")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al iniciar sesión: " + e.getMessage()
            );
        }

        return null;
    }

    public boolean esBibliotecario(Usuario usuario) {
        return usuario != null
                && "bibliotecario".equals(usuario.getRol());
    }

    public boolean esEstudiante(Usuario usuario) {
        return usuario != null
                && "estudiante".equals(usuario.getRol());
    }
}