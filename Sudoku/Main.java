package Sudoku;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Sudoku");
        SwingUtilities.invokeLater(() -> new WelcomeScreen());
    }
}
