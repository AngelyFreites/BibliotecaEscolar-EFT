package vista;

import controlador.PrestamoController;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PrestamosFrame extends JFrame {

    private final PrestamoController controller = new PrestamoController();

    private final JTextField txtIdEstudiante = new JTextField();
    private final JTextField txtIdLibro = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{
                    "ID", "ID estudiante", "ID libro",
                    "Fecha préstamo", "Fecha devolución", "Estado"
            }, 0) {

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    private final JButton btnRegistrar = new JButton("Registrar préstamo");
    private final JButton btnDevolver = new JButton("Registrar devolución");
    private final JButton btnConsultar = new JButton("Consultar préstamos");
    private final JButton btnLimpiar = new JButton("Limpiar");

    public PrestamosFrame() {
        setTitle("Biblioteca Escolar - Préstamos y devoluciones");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 10));
        formulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        formulario.add(new JLabel("ID del estudiante:"));
        formulario.add(txtIdEstudiante);
        formulario.add(new JLabel("ID del libro:"));
        formulario.add(txtIdLibro);

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnRegistrar);
        botones.add(btnDevolver);
        botones.add(btnConsultar);
        botones.add(btnLimpiar);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        btnRegistrar.addActionListener(e -> registrarPrestamo());
        btnDevolver.addActionListener(e -> registrarDevolucion());
        btnConsultar.addActionListener(e -> cargarPrestamos());
        btnLimpiar.addActionListener(e -> limpiar());

        cargarPrestamos();
    }

    private void cargarPrestamos() {
        modeloTabla.setRowCount(0);

        List<Prestamo> prestamos = controller.listarPrestamos();

        for (Prestamo prestamo : prestamos) {
            modeloTabla.addRow(new Object[]{
                    prestamo.getId(),
                    prestamo.getIdEstudiante(),
                    prestamo.getIdLibro(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucion(),
                    prestamo.isDevuelto() ? "Devuelto" : "Pendiente"
            });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        txtIdEstudiante.setText(
                modeloTabla.getValueAt(filaModelo, 1).toString()
        );

        txtIdLibro.setText(
                modeloTabla.getValueAt(filaModelo, 2).toString()
        );
    }

    private void registrarPrestamo() {
        int idEstudiante;
        int idLibro;

        try {
            idEstudiante = Integer.parseInt(
                    txtIdEstudiante.getText().trim()
            );

            idLibro = Integer.parseInt(
                    txtIdLibro.getText().trim()
            );

            if (idEstudiante <= 0 || idLibro <= 0) {
                mostrarMensaje("Ingrese identificadores válidos.");
                return;
            }

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Ingrese números válidos para el estudiante y el libro."
            );
            return;
        }

        btnRegistrar.setEnabled(false);

        controller.registrarPrestamoAsync(
                idEstudiante,
                idLibro,
                LocalDate.now(),
                null,
                resultado -> {
                    btnRegistrar.setEnabled(true);

                    if (resultado) {
                        mostrarMensaje("Préstamo registrado correctamente.");
                        limpiar();
                        cargarPrestamos();
                    } else {
                        mostrarMensaje(
                                "No se pudo registrar el préstamo. "
                                        + "Verifique el estudiante y el stock del libro."
                        );
                    }
                }
        );
    }

    private void registrarDevolucion() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            mostrarMensaje("Seleccione un préstamo para devolver.");
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        String estado = modeloTabla.getValueAt(filaModelo, 5).toString();

        if ("Devuelto".equals(estado)) {
            mostrarMensaje("Este préstamo ya fue devuelto.");
            return;
        }

        int idPrestamo = (int) modeloTabla.getValueAt(filaModelo, 0);

        Object[] opciones = {"Sí", "No"};

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Desea registrar la devolución del préstamo "
                        + idPrestamo + "?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[1]
        );

        if (respuesta != 0) {
            return;
        }

        btnDevolver.setEnabled(false);

        controller.registrarDevolucionAsync(
                idPrestamo,
                LocalDate.now(),
                resultado -> {
                    btnDevolver.setEnabled(true);

                    if (resultado) {
                        mostrarMensaje("Devolución registrada correctamente.");
                        limpiar();
                        cargarPrestamos();
                    } else {
                        mostrarMensaje(
                                "No se pudo registrar la devolución."
                        );
                        cargarPrestamos();
                    }
                }
        );
    }

    private void limpiar() {
        txtIdEstudiante.setText("");
        txtIdLibro.setText("");
        tabla.clearSelection();
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