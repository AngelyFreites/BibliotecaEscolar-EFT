package controlador;

import dao.ReporteDAO;

import javax.swing.table.DefaultTableModel;

public class ReporteController {

    private final ReporteDAO reporteDAO;

    public ReporteController() {
        this.reporteDAO = new ReporteDAO();
    }

    public DefaultTableModel librosMasPrestados() {
        return reporteDAO.librosMasPrestados();
    }

    public DefaultTableModel historialPorEstudiante(int idEstudiante) {
        return reporteDAO.historialPorEstudiante(idEstudiante);
    }

    public DefaultTableModel librosActualmentePrestados() {
        return reporteDAO.librosActualmentePrestados();
    }
}