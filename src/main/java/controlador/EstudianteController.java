package controlador;

import dao.EstudianteDAO;
import modelo.Estudiante;

import java.util.List;

public class EstudianteController {

    private final EstudianteDAO estudianteDAO;

    public EstudianteController() {
        this.estudianteDAO = new EstudianteDAO();
    }

    public boolean agregarEstudiante(Estudiante estudiante) {
        return estudianteDAO.agregar(estudiante);
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteDAO.listar();
    }

    public boolean actualizarEstudiante(Estudiante estudiante) {
        return estudianteDAO.actualizar(estudiante);
    }

    public boolean eliminarEstudiante(int id) {
        return estudianteDAO.eliminar(id);
    }
}