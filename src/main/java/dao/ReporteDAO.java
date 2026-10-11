package dao;

import util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ReporteDAO {

    public DefaultTableModel librosMasPrestados() {
        DefaultTableModel modelo = crearModelo(
                "ID libro", "Título", "Autor", "Total préstamos"
        );

        String sql = """
                SELECT l.id, l.titulo, l.autor,
                       COUNT(p.id) AS total_prestamos
                FROM libros l
                LEFT JOIN prestamos p ON l.id = p.id_libro
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY total_prestamos DESC, l.titulo
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("total_prestamos")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error en reporte de libros: " + e.getMessage());
        }

        return modelo;
    }

    public DefaultTableModel historialPorEstudiante(int idEstudiante) {
        DefaultTableModel modelo = crearModelo(
                "ID préstamo", "Estudiante", "Libro",
                "Fecha préstamo", "Fecha devolución", "Estado"
        );

        String sql = """
                SELECT p.id, e.nombre AS estudiante,
                       l.titulo AS libro, p.fecha_prestamo,
                       p.fecha_devolucion, p.devuelto
                FROM prestamos p
                JOIN estudiantes e ON p.id_estudiante = e.id
                JOIN libros l ON p.id_libro = l.id
                WHERE e.id = ?
                ORDER BY p.fecha_prestamo DESC, p.id DESC
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("estudiante"),
                            rs.getString("libro"),
                            rs.getDate("fecha_prestamo"),
                            rs.getDate("fecha_devolucion"),
                            rs.getBoolean("devuelto")
                                    ? "Devuelto" : "Pendiente"
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en historial: " + e.getMessage());
        }

        return modelo;
    }

    public DefaultTableModel librosActualmentePrestados() {
        DefaultTableModel modelo = crearModelo(
                "ID préstamo", "Estudiante", "Libro",
                "Fecha préstamo", "Estado"
        );

        String sql = """
                SELECT p.id, e.nombre AS estudiante,
                       l.titulo AS libro, p.fecha_prestamo
                FROM prestamos p
                JOIN estudiantes e ON p.id_estudiante = e.id
                JOIN libros l ON p.id_libro = l.id
                WHERE p.devuelto = 0
                ORDER BY p.fecha_prestamo, p.id
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("estudiante"),
                        rs.getString("libro"),
                        rs.getDate("fecha_prestamo"),
                        "Pendiente"
                });
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error en reporte de préstamos activos: " + e.getMessage()
            );
        }

        return modelo;
    }

    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}