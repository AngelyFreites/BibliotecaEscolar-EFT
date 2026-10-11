package vista;

import controlador.LibroController;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrosFrame extends JFrame {

    private final LibroController controller = new LibroController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtIsbn = new JTextField();
    private final JTextField txtEditorial = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JTextField txtCategoria = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Título", "Autor", "ISBN",
                    "Editorial", "Stock", "Categoría"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public LibrosFrame() {
        setTitle("Biblioteca Escolar - Gestión de libros");
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(7, 2, 8, 8));
        formulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        txtId.setEditable(false);

        agregarCampo(formulario, "ID:", txtId);
        agregarCampo(formulario, "Título:", txtTitulo);
        agregarCampo(formulario, "Autor:", txtAutor);
        agregarCampo(formulario, "ISBN:", txtIsbn);
        agregarCampo(formulario, "Editorial:", txtEditorial);
        agregarCampo(formulario, "Stock:", txtStock);
        agregarCampo(formulario, "ID categoría:", txtCategoria);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnConsultar = new JButton("Consultar libros");

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
        btnConsultar.addActionListener(e -> cargarLibros());

        cargarLibros();
    }

    private void agregarCampo(JPanel panel, String etiqueta,
                              JTextField campo) {
        panel.add(new JLabel(etiqueta));
        panel.add(campo);
    }

    private String valorOpcional(String texto) {
        return texto.isBlank() ? null : texto.trim();
    }

    private void cargarLibros() {
        modeloTabla.setRowCount(0);

        List<Libro> libros = controller.listarLibros();

        for (Libro libro : libros) {
            modeloTabla.addRow(new Object[]{
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getEditorial(),
                    libro.getStock(),
                    libro.getIdCategoria()
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
        txtTitulo.setText(textoCelda(filaModelo, 1));
        txtAutor.setText(textoCelda(filaModelo, 2));
        txtIsbn.setText(textoCelda(filaModelo, 3));
        txtEditorial.setText(textoCelda(filaModelo, 4));
        txtStock.setText(textoCelda(filaModelo, 5));
        txtCategoria.setText(textoCelda(filaModelo, 6));
    }

    private String textoCelda(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void guardar(boolean actualizar) {
        if (actualizar && txtId.getText().isBlank()) {
            mostrarMensaje("Seleccione un libro para actualizar.");
            return;
        }

        try {
            int id = actualizar
                    ? Integer.parseInt(txtId.getText()) : 0;

            Integer stock = txtStock.getText().isBlank()
                    ? null : Integer.valueOf(txtStock.getText().trim());

            Integer categoria = txtCategoria.getText().isBlank()
                    ? null : Integer.valueOf(txtCategoria.getText().trim());

            if ((stock != null && stock < 0)
                    || (categoria != null && categoria <= 0)) {
                mostrarMensaje(
                        "El stock no puede ser negativo y la categoría debe ser mayor que cero."
                );
                return;
            }

            Libro libro = new Libro(
                    id,
                    valorOpcional(txtTitulo.getText()),
                    valorOpcional(txtAutor.getText()),
                    valorOpcional(txtIsbn.getText()),
                    valorOpcional(txtEditorial.getText()),
                    stock,
                    categoria
            );

            boolean resultado = actualizar
                    ? controller.actualizarLibro(libro)
                    : controller.agregarLibro(libro);

            if (resultado) {
                mostrarMensaje(actualizar
                        ? "Libro actualizado correctamente."
                        : "Libro agregado correctamente.");

                limpiar();
                cargarLibros();
            } else {
                mostrarMensaje(
                        "No se pudo guardar el libro. Verifique los datos ingresados."
                );
            }

        } catch (NumberFormatException ex) {
            mostrarMensaje(
                    "El stock y el ID de categoría deben ser números enteros."
            );
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            mostrarMensaje("Seleccione un libro para eliminar.");
            return;
        }

        Object[] opciones = {"Sí", "No"};

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Está seguro de que desea eliminar el libro seleccionado?",
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

        if (controller.eliminarLibro(id)) {
            mostrarMensaje("Libro eliminado correctamente.");
            limpiar();
            cargarLibros();
        } else {
            mostrarMensaje(
                    "No se pudo eliminar el libro. Puede tener préstamos asociados."
            );
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        txtCategoria.setText("");
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