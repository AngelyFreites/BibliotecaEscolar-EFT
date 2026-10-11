package dao;

import modelo.Usuario;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean agregar(Usuario usuario) {
        String sql = """
                INSERT INTO usuarios
                (nombre, rut, correo, `contraseña`, rol)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT id, nombre, rut, correo, rol
                FROM usuarios
                ORDER BY id
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rut"),
                        rs.getString("correo"),
                        null,
                        rs.getString("rol")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    public boolean actualizar(Usuario usuario) {
        boolean cambiarContrasena = usuario.getContrasena() != null
                && !usuario.getContrasena().isBlank();

        String sql;

        if (cambiarContrasena) {
            sql = """
                    UPDATE usuarios
                    SET nombre = ?,
                        rut = ?,
                        correo = ?,
                        rol = ?,
                        `contraseña` = ?
                    WHERE id = ?
                    """;
        } else {
            sql = """
                    UPDATE usuarios
                    SET nombre = ?,
                        rut = ?,
                        correo = ?,
                        rol = ?
                    WHERE id = ?
                    """;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getRol());

            if (cambiarContrasena) {
                ps.setString(5, usuario.getContrasena());
                ps.setInt(6, usuario.getId());
            } else {
                ps.setInt(5, usuario.getId());
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}