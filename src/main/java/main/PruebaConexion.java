package main;

import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {

        try (Connection conexion =
                     DatabaseConnection.getInstance().getConnection()) {

            System.out.println("Conexión exitosa a la base de datos biblioteca.");

        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}