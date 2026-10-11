package vista;

import controlador.ReporteController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportesFrame extends JFrame {

    private final ReporteController controller = new ReporteController();

    private final JTable tabla = new JTable();

    private final JTextField txtIdEstudiante = new JTextField(8);

    public ReportesFrame() {
        setTitle("Biblioteca Escolar - Reportes");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel superior = new JPanel(new FlowLayout());

        JButton btnMasPrestados = new JButton("Libros más prestados");
        JButton btnHistorial = new JButton("Historial por estudiante");
        JButton btnActuales = new JButton("Libros actualmente en préstamo");

        superior.add(btnMasPrestados);
        superior.add(new JLabel("ID estudiante:"));
        superior.add(txtIdEstudiante);
        superior.add(btnHistorial);
        superior.add(btnActuales);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);

        btnMasPrestados.addActionListener(e -> mostrarLibrosMasPrestados());
        btnHistorial.addActionListener(e -> mostrarHistorial());
        btnActuales.addActionListener(e -> mostrarPrestamosActuales());

        mostrarLibrosMasPrestados();
    }

    private void mostrarLibrosMasPrestados() {
        DefaultTableModel modelo = controller.librosMasPrestados();
        tabla.setModel(modelo);
    }

    private void mostrarHistorial() {
        int idEstudiante;

        try {
            idEstudiante = Integer.parseInt(
                    txtIdEstudiante.getText().trim()
            );

            if (idEstudiante <= 0) {
                mostrarMensaje("Ingrese un ID de estudiante válido.");
                return;
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Ingrese un número válido para el estudiante.");
            return;
        }

        DefaultTableModel modelo =
                controller.historialPorEstudiante(idEstudiante);

        tabla.setModel(modelo);

        if (modelo.getRowCount() == 0) {
            mostrarMensaje(
                    "No se encontraron préstamos para este estudiante."
            );
        }
    }

    private void mostrarPrestamosActuales() {
        DefaultTableModel modelo =
                controller.librosActualmentePrestados();

        tabla.setModel(modelo);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Biblioteca Escolar",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}