package controlador;

import dao.PrestamoDAO;
import modelo.Prestamo;

import javax.swing.SwingWorker;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class PrestamoController {

    private final PrestamoDAO prestamoDAO;

    public PrestamoController() {
        this.prestamoDAO = new PrestamoDAO();
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoDAO.listar();
    }

    public void registrarPrestamoAsync(
            int idEstudiante,
            int idLibro,
            LocalDate fechaPrestamo,
            LocalDate fechaVencimiento,
            Consumer<Boolean> alFinalizar) {

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() {
                return prestamoDAO.registrarPrestamoSeguro(
                        idEstudiante,
                        idLibro,
                        fechaPrestamo,
                        fechaVencimiento
                );
            }

            @Override
            protected void done() {
                boolean resultado = false;

                try {
                    resultado = get();
                } catch (Exception e) {
                    System.err.println(
                            "Error al registrar préstamo: " + e.getMessage()
                    );
                }

                if (alFinalizar != null) {
                    alFinalizar.accept(resultado);
                }
            }
        };

        worker.execute();
    }

    public void registrarDevolucionAsync(
            int idPrestamo,
            LocalDate fechaDevolucion,
            Consumer<Boolean> alFinalizar) {

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() {
                return prestamoDAO.registrarDevolucionSeguro(
                        idPrestamo,
                        fechaDevolucion
                );
            }

            @Override
            protected void done() {
                boolean resultado = false;

                try {
                    resultado = get();
                } catch (Exception e) {
                    System.err.println(
                            "Error al registrar devolución: " + e.getMessage()
                    );
                }

                if (alFinalizar != null) {
                    alFinalizar.accept(resultado);
                }
            }
        };

        worker.execute();
    }

    public boolean actualizarPrestamo(Prestamo prestamo) {
        return prestamoDAO.actualizar(prestamo);
    }

    public boolean eliminarPrestamo(int id) {
        return prestamoDAO.eliminar(id);
    }
}