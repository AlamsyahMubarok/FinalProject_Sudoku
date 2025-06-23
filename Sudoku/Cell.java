package Sudoku;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
/**
 * The Cell class model the cells of the Sudoku puzzle, by customizing (subclass)
 * the javax.swing.JTextField to include row/column, puzzle number and status.
 */
public class Cell extends JTextField {
    private static final long serialVersionUID = 1L;  // to prevent serial warning

    // Define named constants for JTextField's colors and fonts
    //  to be chosen based on CellStatus
    public static final Color BG_GIVEN = new Color(240, 240, 240); // RGB
    public static final Color FG_GIVEN = Color.BLACK;
    public static final Color FG_NOT_GIVEN = Color.GRAY;
    public static final Color BG_TO_GUESS = Color.YELLOW;
    public static final Color BG_CORRECT_GUESS = new Color(0, 216, 0);
    public static final Color BG_WRONG_GUESS = new Color(216, 0, 0);
    public static final Font FONT_NUMBERS = new Font("OCR A Extended", Font.PLAIN, 28);

    // Define properties (package-visible)
    /**
     * The row and column number [0-8] of this cell
     */
    int row, col;
    /**
     * The puzzle number [1-9] for this cell
     */
    int number;
    /**
     * The status of this cell defined in enum CellStatus
     */
    CellStatus status;

    /**
     * Constructor
     */
    public Cell(int row, int col) {
        super();   // JTextField
        this.row = row;
        this.col = col;
        // Inherited from JTextField: Beautify all the cells once for all
        super.setHorizontalAlignment(JTextField.CENTER);
        super.setFont(FONT_NUMBERS);
    }

    /**
     * Reset this cell for a new game, given the puzzle number and isGiven
     */
    public void newGame(int number, boolean isGiven) {
        this.number = number;
        if (number == 0) {
            setText(""); // Kosongkan sel jika angka adalah 0
            setEditable(true); // Buat sel dapat diisi pemain
            status = CellStatus.TO_GUESS; // Tandai status sebagai TO_GUESS
        } else {
            setText(String.valueOf(number)); // Tampilkan angka jika diberikan
            setEditable(false); // Kunci sel jika angka sudah diberikan
            status = CellStatus.GIVEN; // Tandai status sebagai GIVEN
        }
        paint();
    }




    public void paint() {
        if (status == CellStatus.GIVEN) {
            super.setText(number == 0 ? "" : String.valueOf(number)); // Tampilkan kosong jika 0
            super.setEditable(false); // Tidak dapat diedit
            super.setBackground(BG_GIVEN); // Warna latar belakang
            super.setForeground(FG_GIVEN); // Warna teks
            super.setFont(FONT_NUMBERS);
            super.setHorizontalAlignment(JTextField.CENTER); // Penyusunan teks di tengah
        } else if (status == CellStatus.TO_GUESS) {
            super.setText(""); // Sel kosong tidak menampilkan angka
            super.setEditable(true); // Bisa diedit
            super.setBackground(BG_TO_GUESS); // Warna latar belakang untuk sel kosong
            super.setForeground(FG_NOT_GIVEN); // Warna teks
            super.setFont(FONT_NUMBERS);
            super.setHorizontalAlignment(JTextField.CENTER); // Penyusunan teks di tengah
        } else if (status == CellStatus.CORRECT_GUESS) {
            super.setBackground(BG_CORRECT_GUESS); // Warna untuk tebakan benar
        } else if (status == CellStatus.WRONG_GUESS) {
            super.setBackground(BG_WRONG_GUESS); // Warna untuk tebakan salah
        }
    }


}