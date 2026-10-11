package main;

import vista.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            LoginFrame ventana = new LoginFrame();
            ventana.setVisible(true);
        });

    }
}