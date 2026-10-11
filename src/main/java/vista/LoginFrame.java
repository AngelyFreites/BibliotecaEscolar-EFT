package vista;

import controlador.AutenticacionController;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginFrame extends JFrame {

    private final JTextField txtRut;
    private final JPasswordField txtContrasena;
    private final AutenticacionController autenticacionController;

    public LoginFrame() {
        autenticacionController = new AutenticacionController();

        setTitle("Biblioteca Escolar - Inicio de sesión");
        setSize(430, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("BIBLIOTECA ESCOLAR", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("RUT:"), gbc);

        txtRut = new JTextField(17);
        gbc.gridx = 1;
        panel.add(txtRut, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);

        txtContrasena = new JPasswordField(17);
        gbc.gridx = 1;
        panel.add(txtContrasena, gbc);

        JButton btnIngresar = new JButton("Iniciar sesión");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        btnIngresar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(btnIngresar);

        add(panel);
    }

    private void iniciarSesion() {
        String rut = txtRut.getText().trim();
        char[] clave = txtContrasena.getPassword();

        if (rut.isBlank() || clave.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese su RUT y contraseña.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE);
            Arrays.fill(clave, '\0');
            return;
        }

        Usuario usuario;

        try {
            usuario = autenticacionController.iniciarSesion(
                    rut, new String(clave));
        } finally {
            Arrays.fill(clave, '\0');
            txtContrasena.setText("");
        }

        if (usuario == null) {
            JOptionPane.showMessageDialog(this,
                    "RUT o contraseña incorrectos.",
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rol = usuario.getRol();

        JOptionPane.showMessageDialog(this,
                "Bienvenido/a, " + usuario.getNombre()
                        + "\nRol: " + rol,
                "Inicio de sesión exitoso",
                JOptionPane.INFORMATION_MESSAGE);

        MenuPrincipalFrame menu = new MenuPrincipalFrame(usuario);
        menu.setVisible(true);
        dispose();
    }
}