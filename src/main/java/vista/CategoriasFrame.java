package vista;

import controlador.CategoriaController;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriasFrame extends JFrame {

    private final CategoriaController controller = new CategoriaController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre de la categoría"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public CategoriasFrame() {
        setTitle("Biblioteca Escolar - Gestión de categorías");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 10));
        formulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Nombre de la categoría:"));
        formulario.add(txtNombre);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnConsultar = new JButton("Consultar categorías");

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
        btnConsultar.addActionListener(e -> cargarCategorias());

        cargarCategorias();
    }

    private void cargarCategorias() {
        modeloTabla.setRowCount(0);

        List<Categoria> categorias = controller.listarCategorias();

        for (Categoria categoria : categorias) {
            modeloTabla.addRow(new Object[]{
                    categoria.getId(),
                    categoria.getNombre()
            });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);

        txtId.setText(
                modeloTabla.getValueAt(filaModelo, 0).toString()
        );

        Object nombre = modeloTabla.getValueAt(filaModelo, 1);
        txtNombre.setText(nombre == null ? "" : nombre.toString());
    }

    private void guardar(boolean actualizar) {
        if (actualizar && txtId.getText().isBlank()) {
            mostrarMensaje("Seleccione una categoría para actualizar.");
            return;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Ingrese el nombre de la categoría.");
            return;
        }

        int id = actualizar
                ? Integer.parseInt(txtId.getText()) : 0;

        Categoria categoria = new Categoria(id, nombre);

        boolean resultado = actualizar
                ? controller.actualizarCategoria(categoria)
                : controller.agregarCategoria(categoria);

        if (resultado) {
            mostrarMensaje(actualizar
                    ? "Categoría actualizada correctamente."
                    : "Categoría agregada correctamente.");

            limpiar();
            cargarCategorias();
        } else {
            mostrarMensaje(
                    "No se pudo guardar la categoría. Verifique los datos."
            );
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            mostrarMensaje("Seleccione una categoría para eliminar.");
            return;
        }

        Object[] opciones = {"Sí", "No"};

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Está seguro de que desea eliminar la categoría seleccionada?",
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

        if (controller.eliminarCategoria(id)) {
            mostrarMensaje("Categoría eliminada correctamente.");
            limpiar();
            cargarCategorias();
        } else {
            mostrarMensaje(
                    "No se pudo eliminar la categoría. Puede tener libros asociados."
            );
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
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