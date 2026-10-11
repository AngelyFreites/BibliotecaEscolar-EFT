package controlador;

import dao.CategoriaDAO;
import modelo.Categoria;

import java.util.List;

public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public boolean agregarCategoria(Categoria categoria) {
        return categoriaDAO.agregar(categoria);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    public boolean actualizarCategoria(Categoria categoria) {
        return categoriaDAO.actualizar(categoria);
    }

    public boolean eliminarCategoria(int id) {
        return categoriaDAO.eliminar(id);
    }
}