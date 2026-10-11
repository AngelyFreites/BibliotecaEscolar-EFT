package controlador;

import dao.LibroDAO;
import modelo.Libro;

import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO;

    public LibroController() {
        this.libroDAO = new LibroDAO();
    }

    public boolean agregarLibro(Libro libro) {
        return libroDAO.agregar(libro);
    }

    public List<Libro> listarLibros() {
        return libroDAO.listar();
    }

    public boolean actualizarLibro(Libro libro) {
        return libroDAO.actualizar(libro);
    }

    public boolean eliminarLibro(int id) {
        return libroDAO.eliminar(id);
    }
}