package Sudoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class Sudoku extends JFrame {
    private static final long serialVersionUID = 1L;
    private GameBoardPanel board;
    private int currentDifficulty;
    private Clip backgroundClip;
    private Clip correctSoundClip;
    private Clip incorrectSoundClip;
    private Timer gameTimer;
    private int elapsedTime = 0; // in seconds
    private JLabel timerLabel;
    private boolean isPaused = false; // Track if the timer is paused
    private JButton pauseResumeButton; // Button to pause/resume the timer
    private JButton hintButton; // Button to provide hints
    private int hintCounter = 0; // Track the number of hints used
    private int maxHints; // Maximum number of hints allowed
    private String playerName;
    private int score;
    private JLabel playerNameLabel;
    private JLabel scoreLabel;

    public Sudoku(int difficulty, String playerName) {
        this.currentDifficulty = difficulty;
        setMaxHints(difficulty); // Set the maximum hints based on difficulty
        this.playerName = playerName;
        this.score = 0; // Initialize score

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        // Play background music
        playBackgroundMusic("Sudoku/backsound.wav");

        // Load sound effects
        correctSoundClip = loadSound("Sudoku/benar sudoku.wav");
        incorrectSoundClip = loadSound("Sudoku/salah sudoku.wav");

        // Create Menu Bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem resetGameItem = new JMenuItem("Reset Game");
        JMenuItem exitItem = new JMenuItem("Exit to Main Menu");

        gameMenu.add(newGameItem);
        gameMenu.add(resetGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        // Options Menu
        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        JMenuItem volumeControl = new JMenuItem("Volume Control");
        optionsMenu.add(volumeControl);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        // Add Action Listeners
        newGameItem.addActionListener(e -> showDifficultySelection());
        resetGameItem.addActionListener(e -> resetGame());
        exitItem.addActionListener(e -> {
            dispose(); // Close the current Sudoku window
            SwingUtilities.invokeLater(() -> new WelcomeScreen()); // Open the WelcomeScreen
        });
        volumeControl.addActionListener(e -> showVolumeControl());
        aboutItem.addActionListener(e -> showAboutDialog());

        // Timer and Pause/Resume Button Panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timerPanel.setBackground(new Color(52, 73, 94)); // Dark background for contrast
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setForeground(Color.WHITE); // White text for visibility
        timerLabel.setFont(new Font("Montserrat", Font.BOLD, 16));

        pauseResumeButton = createStyledButton("Pause");
        pauseResumeButton.addActionListener(e -> togglePauseResume(pauseResumeButton));

        hintButton = createStyledButton("Hint (" + (maxHints - hintCounter) + " left)");
        hintButton.addActionListener(e -> {
            if (hintCounter < maxHints) {
                board.provideHint();
                hintCounter++;
                updateHintButtonText();
            } else {
                JOptionPane.showMessageDialog(this, "No more hints available!");
            }
        });

        timerPanel.add(timerLabel);
        timerPanel.add(pauseResumeButton);
        timerPanel.add(hintButton);

        // Player info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(52, 73, 94)); // Dark background for contrast
        playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setForeground(Color.WHITE);
        playerNameLabel.setFont(new Font("Montserrat", Font.BOLD, 16));

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Montserrat", Font.BOLD, 16));

        infoPanel.add(playerNameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(20, 0))); // Spacer
        infoPanel.add(scoreLabel);

        cp.add(infoPanel, BorderLayout.NORTH);
        cp.add(timerPanel, BorderLayout.SOUTH);
        board = new GameBoardPanel(this); // Pass Sudoku instance to GameBoardPanel
        cp.add(board, BorderLayout.CENTER);

        startNewGame(difficulty);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setVisible(true);
    }

    private Clip loadSound(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();
        }
    }

    private void setMaxHints(int difficulty) {
        switch (difficulty) {
            case SudokuConstants.EASY:
                maxHints = 3;
                break;
            case SudokuConstants.MEDIUM:
                maxHints = 5;
                break;
            case SudokuConstants.HARD:
                maxHints = 7;
                break;
            default:
                maxHints = 3;
        }
    }

    private void updateHintButtonText() {
        hintButton.setText("Hint (" + (maxHints - hintCounter) + " left)");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185)); // Primary color
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219)); // Secondary color
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185)); // Primary color
            }
        });

        return button;
    }

    private void startTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    elapsedTime++;
                    int minutes = elapsedTime / 60;
                    int seconds = elapsedTime % 60;
                    timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
                }
            }
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void pauseTimer() {
        isPaused = true;
    }

    private void resumeTimer() {
        isPaused = false;
    }

    private void togglePauseResume(JButton pauseResumeButton) {
        if (isPaused) {
            resumeTimer();
            pauseResumeButton.setText("Pause");
        } else {
            pauseTimer();
            pauseResumeButton.setText("Resume");
        }
    }

    private void playBackgroundMusic(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);

            // Use VolumeManager to manage volume
            VolumeManager.getInstance().setCurrentClip(backgroundClip);

            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVolumeControl() {
        JDialog volumeDialog = new JDialog(this, "Volume Control", true);
        volumeDialog.setLayout(new BorderLayout(10, 10));
        volumeDialog.setSize(300, 150);

        JPanel volumePanel = new JPanel(new BorderLayout(10, 10));
        volumePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel volumeLabel = new JLabel("Volume: ");
        volumeLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        volumePanel.add(volumeLabel, BorderLayout.NORTH);

        JSlider volumeSlider = new JSlider(0, 100, (int)(VolumeManager.getInstance().getVolume() * 100));
        volumeSlider.addChangeListener(e -> {
            float newVolume = volumeSlider.getValue() / 100f;
            VolumeManager.getInstance().setVolume(newVolume);
        });

        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> volumeDialog.dispose());

        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.add(closeButton, BorderLayout.SOUTH);

        volumeDialog.add(volumePanel);
        volumeDialog.setLocationRelativeTo(this);
        volumeDialog.setVisible(true);
    }

    private void resetGame() {
        stopTimer();
        elapsedTime = 0;
        timerLabel.setText("Time: 00:00");
        board.newGame(currentDifficulty);
        startTimer();
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "About", true);
        aboutDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Sudoku Game");
        JLabel versionLabel = new JLabel("Version 1.0");
        JLabel teamLabel = new JLabel("Created by Team 2");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        teamLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(versionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(teamLabel);

        JButton closeButton = createStyledButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> aboutDialog.dispose());

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(closeButton);

        aboutDialog.add(panel);
        aboutDialog.setSize(300, 200);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }

    private void showDifficultySelection() {
        JDialog difficultyDialog = new JDialog(this, "Select Difficulty", true);
        difficultyDialog.setLayout(new GridLayout(3, 1, 10, 10));
        difficultyDialog.setSize(300, 200);

        JButton btnEasy = createStyledButton("Easy");
        JButton btnMedium = createStyledButton("Medium");
        JButton btnHard = createStyledButton("Hard");

        btnEasy.addActionListener(e -> {
            startNewGame(SudokuConstants.EASY);
            difficultyDialog.dispose();
        });
        btnMedium.addActionListener(e -> {
            startNewGame(SudokuConstants.MEDIUM);
            difficultyDialog.dispose();
        });
        btnHard.addActionListener(e -> {
            startNewGame(SudokuConstants.HARD);
            difficultyDialog.dispose();
        });

        difficultyDialog.add(btnEasy);
        difficultyDialog.add(btnMedium);
        difficultyDialog.add(btnHard);

        difficultyDialog.setLocationRelativeTo(this);
        difficultyDialog.setVisible(true);
    }

    private void startNewGame(int difficulty) {
        stopTimer();
        elapsedTime = 0;
        timerLabel.setText("Time: 00:00");
        int cellsToGuess;
        switch (difficulty) {
            case SudokuConstants.EASY: cellsToGuess = SudokuConstants.EASY; break;
            case SudokuConstants.MEDIUM: cellsToGuess = SudokuConstants.MEDIUM; break;
            case SudokuConstants.HARD: cellsToGuess = SudokuConstants.HARD; break;
            default: cellsToGuess = SudokuConstants.EASY;
        }
        board.newGame(cellsToGuess);
        startTimer();
    }

    @Override
    public void dispose() {
        stopTimer();
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
        super.dispose();
    }

    public void updateScore(int points) {
        score += points;
        scoreLabel.setText("Score: " + score);
    }

    public void playCorrectSound() {
        playSound(correctSoundClip);
    }

    public void playIncorrectSound() {
        playSound(incorrectSoundClip);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen());
    }
}