package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private final Usuario usuarioActual;

    public MenuPrincipalFrame(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("Biblioteca Escolar - Menú principal");
        setSize(550, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel titulo = new JLabel(
                "SISTEMA BIBLIOTECA ESCOLAR",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel bienvenida = new JLabel(
                "<html><center>Bienvenido/a, "
                        + usuarioActual.getNombre()
                        + "<br>Rol: "
                        + usuarioActual.getRol()
                        + "</center></html>",
                SwingConstants.CENTER
        );

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 5, 5));
        encabezado.add(titulo);
        encabezado.add(bienvenida);

        panel.add(encabezado, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(7, 1, 10, 10));

        JButton btnLibros = new JButton("Gestión de libros");
        JButton btnEstudiantes = new JButton("Gestión de estudiantes");
        JButton btnCategorias = new JButton("Gestión de categorías");
        JButton btnUsuarios = new JButton("Gestión de usuarios");
        JButton btnPrestamos = new JButton("Préstamos y devoluciones");
        JButton btnReportes = new JButton("Reportes");
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        botones.add(btnLibros);
        botones.add(btnEstudiantes);
        botones.add(btnCategorias);
        botones.add(btnUsuarios);
        botones.add(btnPrestamos);
        botones.add(btnReportes);
        botones.add(btnCerrarSesion);

        boolean esBibliotecario =
                "bibliotecario".equalsIgnoreCase(
                        usuarioActual.getRol()
                );

        // Restricciones visuales según el rol
        btnLibros.setEnabled(esBibliotecario);
        btnEstudiantes.setEnabled(esBibliotecario);
        btnCategorias.setEnabled(esBibliotecario);
        btnUsuarios.setEnabled(esBibliotecario);

        // Los reportes administrativos son exclusivos
        // del bibliotecario.
        btnReportes.setEnabled(esBibliotecario);

        // Funciones que conectaremos con las ventanas
        btnLibros.addActionListener(e ->
                new LibrosFrame().setVisible(true));

        btnEstudiantes.addActionListener(e ->
                new EstudiantesFrame().setVisible(true));

        btnCategorias.addActionListener(e ->
                new CategoriasFrame().setVisible(true));

        btnUsuarios.addActionListener(e ->
                new UsuariosFrame().setVisible(true));

        btnPrestamos.addActionListener(e ->
                new PrestamosFrame().setVisible(true));

        btnReportes.addActionListener(e ->
                new ReportesFrame().setVisible(true));

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        panel.add(botones, BorderLayout.CENTER);

        add(panel);
    }

    private void mostrarPendiente(String modulo) {
        JOptionPane.showMessageDialog(
                this,
                "Próximamente: " + modulo,
                "Módulo en desarrollo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}