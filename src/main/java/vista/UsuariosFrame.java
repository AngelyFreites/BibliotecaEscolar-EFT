package vista;

import controlador.UsuarioController;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class UsuariosFrame extends JFrame {

    private final UsuarioController controller = new UsuarioController();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRut = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JPasswordField txtContrasena = new JPasswordField();

    private final JComboBox<String> cmbRol = new JComboBox<>(
            new String[]{"Bibliotecario", "Estudiante"}
    );

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "RUT", "Correo", "Rol"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modeloTabla);

    public UsuariosFrame() {
        setTitle("Biblioteca Escolar - Gestión de usuarios");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 8));
        formulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("RUT:"));
        formulario.add(txtRut);
        formulario.add(new JLabel("Correo:"));
        formulario.add(txtCorreo);
        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtContrasena);
        formulario.add(new JLabel("Rol:"));
        formulario.add(cmbRol);

        JPanel botones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnConsultar = new JButton("Consultar usuarios");

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
        btnConsultar.addActionListener(e -> cargarUsuarios());

        cargarUsuarios();
    }

    private String valorOpcional(String texto) {
        return texto.isBlank() ? null : texto.trim();
    }

    private String obtenerRol() {
        String rolSeleccionado = (String) cmbRol.getSelectedItem();

        if ("Bibliotecario".equals(rolSeleccionado)) {
            return "bibliotecario";
        }

        return "estudiante";
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);

        List<Usuario> usuarios = controller.listarUsuarios();

        for (Usuario usuario : usuarios) {
            String rol = "bibliotecario".equals(usuario.getRol())
                    ? "Bibliotecario" : "Estudiante";

            modeloTabla.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getRut(),
                    usuario.getCorreo(),
                    rol
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
        txtCorreo.setText(textoCelda(filaModelo, 3));

        cmbRol.setSelectedItem(textoCelda(filaModelo, 4));
        txtContrasena.setText("");
    }

    private String textoCelda(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void guardar(boolean actualizar) {
        if (actualizar && txtId.getText().isBlank()) {
            mostrarMensaje("Seleccione un usuario para actualizar.");
            return;
        }

        String nombre = valorOpcional(txtNombre.getText());
        String rut = valorOpcional(txtRut.getText());
        String correo = valorOpcional(txtCorreo.getText());

        char[] caracteres = txtContrasena.getPassword();
        String contrasena = new String(caracteres);
        Arrays.fill(caracteres, '\0');

        if (nombre == null || rut == null) {
            mostrarMensaje("Ingrese el nombre y RUT del usuario.");
            return;
        }

        if (!actualizar && contrasena.isBlank()) {
            mostrarMensaje("Ingrese una contraseña para el nuevo usuario.");
            return;
        }

        if (contrasena.isBlank()) {
            contrasena = null;
        }

        int id = actualizar
                ? Integer.parseInt(txtId.getText()) : 0;

        Usuario usuario = new Usuario(
                id,
                nombre,
                rut,
                correo,
                contrasena,
                obtenerRol()
        );

        boolean resultado = actualizar
                ? controller.actualizarUsuario(usuario)
                : controller.agregarUsuario(usuario);

        if (resultado) {
            mostrarMensaje(actualizar
                    ? "Usuario actualizado correctamente."
                    : "Usuario agregado correctamente.");

            limpiar();
            cargarUsuarios();
        } else {
            mostrarMensaje(
                    "No se pudo guardar el usuario. Verifique los datos y el RUT."
            );
        }
    }

    private void eliminar() {
        if (txtId.getText().isBlank()) {
            mostrarMensaje("Seleccione un usuario para eliminar.");
            return;
        }

        Object[] opciones = {"Sí", "No"};

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Está seguro de que desea eliminar el usuario seleccionado?",
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

        if (controller.eliminarUsuario(id)) {
            mostrarMensaje("Usuario eliminado correctamente.");
            limpiar();
            cargarUsuarios();
        } else {
            mostrarMensaje("No se pudo eliminar el usuario.");
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtRut.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);
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