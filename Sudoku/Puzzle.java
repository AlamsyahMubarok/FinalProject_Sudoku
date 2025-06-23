package Sudoku;

public class Puzzle {
    int[][] numbers = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    boolean[][] isGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    int[][] solution = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    
    public void newPuzzle(int difficulty) {
        // Generate solution first
        generateSolution();
        
        // Copy solution to numbers
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.GRID_SIZE; j++) {
                numbers[i][j] = solution[i][j];
                isGiven[i][j] = true;
            }
        }
        
        // Remove numbers according to difficulty
        int cellsToRemove = difficulty;
        while (cellsToRemove > 0) {
            int row = (int)(Math.random() * SudokuConstants.GRID_SIZE);
            int col = (int)(Math.random() * SudokuConstants.GRID_SIZE);
            if (numbers[row][col] != 0) {
                numbers[row][col] = 0;
                isGiven[row][col] = false;
                cellsToRemove--;
            }
        }
    }

    private void generateSolution() {
        // Clear the grid
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.GRID_SIZE; j++) {
                solution[i][j] = 0;
            }
        }
        
        // Fill diagonal 3x3 boxes first (they are independent)
        fillDiagonalBoxes();
        
        // Fill remaining cells
        fillRemaining(0, SudokuConstants.SUBGRID_SIZE);
    }

    private void fillDiagonalBoxes() {
        for (int box = 0; box < SudokuConstants.GRID_SIZE; box += SudokuConstants.SUBGRID_SIZE) {
            fillBox(box, box);
        }
    }

    private void fillBox(int row, int col) {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(numbers);
        int num = 0;
        
        for (int i = 0; i < SudokuConstants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.SUBGRID_SIZE; j++) {
                solution[row + i][col + j] = numbers[num++];
            }
        }
    }

    private void shuffleArray(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    private boolean fillRemaining(int row, int col) {
        if (col >= SudokuConstants.GRID_SIZE && row < SudokuConstants.GRID_SIZE - 1) {
            row++;
            col = 0;
        }
        if (row >= SudokuConstants.GRID_SIZE && col >= SudokuConstants.GRID_SIZE) {
            return true;
        }
        if (row < SudokuConstants.SUBGRID_SIZE) {
            if (col < SudokuConstants.SUBGRID_SIZE) {
                col = SudokuConstants.SUBGRID_SIZE;
            }
        } else if (row < SudokuConstants.GRID_SIZE - SudokuConstants.SUBGRID_SIZE) {
            if (col == (int)(row / SudokuConstants.SUBGRID_SIZE) * SudokuConstants.SUBGRID_SIZE) {
                col += SudokuConstants.SUBGRID_SIZE;
            }
        } else {
            if (col == SudokuConstants.GRID_SIZE - SudokuConstants.SUBGRID_SIZE) {
                row++;
                col = 0;
                if (row >= SudokuConstants.GRID_SIZE) {
                    return true;
                }
            }
        }

        for (int num = 1; num <= SudokuConstants.GRID_SIZE; num++) {
            if (isSafe(row, col, num)) {
                solution[row][col] = num;
                if (fillRemaining(row, col + 1)) {
                    return true;
                }
                solution[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int row, int col, int num) {
        // Check row
        for (int x = 0; x < SudokuConstants.GRID_SIZE; x++) {
            if (solution[row][x] == num) {
                return false;
            }
        }

        // Check column
        for (int x = 0; x < SudokuConstants.GRID_SIZE; x++) {
            if (solution[x][col] == num) {
                return false;
            }
        }

        // Check 3x3 box
        int startRow = row - row % SudokuConstants.SUBGRID_SIZE;
        int startCol = col - col % SudokuConstants.SUBGRID_SIZE;
        for (int i = 0; i < SudokuConstants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.SUBGRID_SIZE; j++) {
                if (solution[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }

        return true;
    }
}