package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.util.List;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean agregarUsuario(Usuario usuario) {
        return usuarioDAO.agregar(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public boolean actualizarUsuario(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminarUsuario(int id) {
        return usuarioDAO.eliminar(id);
    }
}