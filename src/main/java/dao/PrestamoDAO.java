
package dao;

import modelo.Prestamo;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public boolean agregar(Prestamo prestamo) {
        if (prestamo == null || prestamo.isDevuelto()
                || prestamo.getFechaDevolucion() != null) {
            return false;
        }

        return registrarPrestamoSeguro(
                prestamo.getIdEstudiante(),
                prestamo.getIdLibro(),
                prestamo.getFechaPrestamo(),
                null
        );
    }

    public List<Prestamo> listar() {
        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
                SELECT id, id_estudiante, id_libro,
                       fecha_prestamo, fecha_devolucion, devuelto
                FROM prestamos
                ORDER BY id
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date fechaPrestamo = rs.getDate("fecha_prestamo");
                Date fechaDevolucion = rs.getDate("fecha_devolucion");

                prestamos.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        fechaPrestamo == null
                                ? null : fechaPrestamo.toLocalDate(),
                        fechaDevolucion == null
                                ? null : fechaDevolucion.toLocalDate(),
                        rs.getBoolean("devuelto")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
        }

        return prestamos;
    }

    public boolean actualizar(Prestamo prestamo) {
        if (prestamo == null) {
            return false;
        }

        if (prestamo.isDevuelto()) {
            return registrarDevolucionSeguro(
                    prestamo.getId(),
                    prestamo.getFechaDevolucion() == null
                            ? LocalDate.now()
                            : prestamo.getFechaDevolucion()
            );
        }

        return false;
    }

    public boolean eliminar(int id) {
        String sql = """
                DELETE FROM prestamos
                WHERE id = ? AND devuelto = 1
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean registrarPrestamoSeguro(
            int idEstudiante,
            int idLibro,
            LocalDate fechaPrestamo,
            LocalDate fechaVencimiento) {

        if (idEstudiante <= 0 || idLibro <= 0
                || fechaPrestamo == null) {
            return false;
        }

        String descontarStock = """
                UPDATE libros
                SET stock = stock - 1
                WHERE id = ? AND stock > 0
                """;

        String insertarPrestamo = """
                INSERT INTO prestamos
                (id_estudiante, id_libro, fecha_prestamo,
                 fecha_devolucion, devuelto)
                VALUES (?, ?, ?, NULL, 0)
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psStock =
                         conn.prepareStatement(descontarStock);
                 PreparedStatement psPrestamo =
                         conn.prepareStatement(insertarPrestamo)) {

                psStock.setInt(1, idLibro);

                if (psStock.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                psPrestamo.setInt(1, idEstudiante);
                psPrestamo.setInt(2, idLibro);
                psPrestamo.setDate(3, Date.valueOf(fechaPrestamo));

                if (psPrestamo.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println(
                        "Error al registrar préstamo: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean registrarDevolucionSeguro(
            int idPrestamo, LocalDate fechaDevolucion) {

        if (idPrestamo <= 0 || fechaDevolucion == null) {
            return false;
        }

        String consultarPrestamo = """
                SELECT id_libro, fecha_prestamo
                FROM prestamos
                WHERE id = ? AND devuelto = 0
                FOR UPDATE
                """;

        String actualizarPrestamo = """
                UPDATE prestamos
                SET devuelto = 1, fecha_devolucion = ?
                WHERE id = ? AND devuelto = 0
                """;

        String aumentarStock = """
                UPDATE libros
                SET stock = stock + 1
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psConsulta =
                         conn.prepareStatement(consultarPrestamo);
                 PreparedStatement psDevolucion =
                         conn.prepareStatement(actualizarPrestamo);
                 PreparedStatement psStock =
                         conn.prepareStatement(aumentarStock)) {

                psConsulta.setInt(1, idPrestamo);

                int idLibro;

                try (ResultSet rs = psConsulta.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    Date fechaOriginal = rs.getDate("fecha_prestamo");

                    if (fechaOriginal != null
                            && fechaDevolucion.isBefore(
                            fechaOriginal.toLocalDate())) {
                        conn.rollback();
                        return false;
                    }

                    idLibro = rs.getInt("id_libro");
                }

                psDevolucion.setDate(1, Date.valueOf(fechaDevolucion));
                psDevolucion.setInt(2, idPrestamo);

                if (psDevolucion.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                psStock.setInt(1, idLibro);

                if (psStock.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println(
                        "Error al registrar devolución: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}