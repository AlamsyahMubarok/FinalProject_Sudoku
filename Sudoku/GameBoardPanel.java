package Sudoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serial warning

    public static final int CELL_SIZE = 60;
    public static final int BOARD_WIDTH = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;

    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private Puzzle puzzle = new Puzzle();
    private Sudoku sudoku; // Reference to the Sudoku instance

    public GameBoardPanel(Sudoku sudoku) {
        this.sudoku = sudoku;
        super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));
        CellInputListener listener = new CellInputListener();

        // Allocate the 2D array of Cell, and added into JPanel.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col);
                super.add(cells[row][col]); // JPanel
            }
        }

        // Add listener to editable cells
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].addActionListener(listener);
                }
            }
        }

        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }

    // Generate a new puzzle and reset the game board of cells based on the puzzle.
    public void newGame(int difficulty) {
        puzzle.newPuzzle(difficulty); // Generate a new puzzle
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (!puzzle.isGiven[row][col]) {
                    // Jika sel kosong (angka 0), buat editable untuk diisi oleh pemain
                    cells[row][col].newGame(0, false);
                } else {
                    // Jika sel diberikan (angka sudah ada), kunci dan tampilkan angka
                    cells[row][col].newGame(puzzle.numbers[row][col], true);
                }
            }
        }
    }

    // Check if the puzzle is solved
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    public void provideHint() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                Cell cell = cells[row][col];
                if (cell.status == CellStatus.TO_GUESS) {
                    int correctNumber = puzzle.solution[row][col];
                    cell.setText(String.valueOf(correctNumber));
                    cell.status = CellStatus.CORRECT_GUESS;
                    cell.setEditable(false); // Make the cell non-editable after hint
                    cell.paint();
                    return; // Provide only one hint at a time
                }
            }
        }
        JOptionPane.showMessageDialog(null, "No more hints available!");
    }

    private class CellInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Cell sourceCell = (Cell) e.getSource();
            int numberIn;
            try {
                numberIn = Integer.parseInt(sourceCell.getText()); // Ambil input pemain
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a number between 1 and 9!");
                sourceCell.setText(""); // Reset input jika tidak valid
                return;
            }

            // Validasi bahwa input berada dalam rentang 1-9
            if (numberIn < 1 || numberIn > 9) {
                JOptionPane.showMessageDialog(null, "Please enter a number between 1 and 9!");
                sourceCell.setText(""); // Reset input jika tidak valid
                return;
            }

            // Periksa apakah jawaban benar menggunakan puzzle.solution
            if (numberIn == puzzle.solution[sourceCell.row][sourceCell.col]) {
                sourceCell.status = CellStatus.CORRECT_GUESS; // Jawaban benar
                sudoku.updateScore(10); // Update score by 10 points for correct guess
            } else {
                sourceCell.status = CellStatus.WRONG_GUESS; // Jawaban salah
            }

            sourceCell.paint(); // Perbarui tampilan sel

            // Periksa apakah teka-teki sudah selesai
            if (isSolved()) {
                JOptionPane.showMessageDialog(null, "Congratulations! You solved the puzzle!");
            }
        }
    }
}