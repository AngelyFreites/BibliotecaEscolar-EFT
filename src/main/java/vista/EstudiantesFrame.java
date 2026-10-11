package vista;

import controlador.EstudianteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EstudiantesFrame extends JFrame {

    private final EstudianteController controller = new EstudianteController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRut = new JTextField();
    private final JTextField txtCurso = new JTextField();
    private final JTextField txtCorreo = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public EstudiantesFrame() {
        setTitle("Biblioteca Escolar - Gestión de estudiantes");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));
        formulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        txtId.setEditable(false);

        agregarCampo(formulario, "ID:", txtId);
        agregarCampo(formulario, "Nombre:", txtNombre);
        agregarCampo(formulario, "RUT:", txtRut);
        agregarCampo(formulario, "Curso:", txtCurso);
        agregarCampo(formulario, "Correo:", txtCorreo);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnConsultar = new JButton("Consultar estudiantes");

        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnConsultar);

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

        btnAgregar.addActionListener(e -> guardar(false));
        btnActualizar.addActionListener(e -> guardar(true));
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnConsultar.addActionListener(e -> cargarEstudiantes());

        cargarEstudiantes();
    }

    private void agregarCampo(JPanel panel, String etiqueta,
                              JTextField campo) {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    private String valorOpcional(String texto) {
        return texto.isBlank() ? null : texto.trim();
    }

    private void cargarEstudiantes() {
        modeloTabla.setRowCount(0);

        List<Estudiante> estudiantes = controller.listarEstudiantes();

        for (Estudiante estudiante : estudiantes) {
            modeloTabla.addRow(new Object[]{
                    estudiante.getId(),
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCurso(),
                    estudiante.getCorreo()
            });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        txtId.setText(textoCelda(filaModelo, 0));
        txtNombre.setText(textoCelda(filaModelo, 1));
        txtRut.setText(textoCelda(filaModelo, 2));
        txtCurso.setText(textoCelda(filaModelo, 3));
        txtCorreo.setText(textoCelda(filaModelo, 4));
    }

    private String textoCelda(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void guardar(boolean actualizar) {
        if (actualizar && txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un estudiante para actualizar.");
            return;
        }

        String nombre = valorOpcional(txtNombre.getText());
        String rut = valorOpcional(txtRut.getText());
        String curso = valorOpcional(txtCurso.getText());
        String correo = valorOpcional(txtCorreo.getText());

        if (rut == null) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el RUT del estudiante.");
            return;
        }

        int id = actualizar
                ? Integer.parseInt(txtId.getText()) : 0;

        Estudiante estudiante = new Estudiante(
                id, nombre, rut, curso, correo
        );

        boolean resultado = actualizar
                ? controller.actualizarEstudiante(estudiante)
                : controller.agregarEstudiante(estudiante);

        if (resultado) {
            JOptionPane.showMessageDialog(this,
                    actualizar ? "Estudiante actualizado correctamente."
                            : "Estudiante agregado correctamente.");
            limpiar();
            cargarEstudiantes();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el estudiante. Verifique los datos y el RUT.");
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un estudiante para eliminar.");
            return;
        }

        Object[] opciones = {"Sí", "No"};

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Está seguro de que desea eliminar el estudiante seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[1]

        );

        if (respuesta != 0) {
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        if (controller.eliminarEstudiante(id)) {
            JOptionPane.showMessageDialog(this,
                    "Estudiante eliminado correctamente.");
            limpiar();
            cargarEstudiantes();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el estudiante. Puede tener préstamos asociados.");
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        tabla.clearSelection();
    }
}