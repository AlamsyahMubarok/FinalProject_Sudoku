package Sudoku;

import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class WelcomeScreen extends JFrame {
    private static final long serialVersionUID = 1L;
    private Color backgroundColor = new Color(128, 0, 128); // Purple background
    private Color textColor = Color.WHITE; // White text for player name
    private Clip backgroundClip;
    private JSlider volumeSlider;
    private JTextField playerNameField;
    private JLabel lblWelcome;
    private Image originalImage;

    public WelcomeScreen() {
        setTitle("Sudoku Game");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Play background music
        playBackgroundMusic("src\\Sudoku\\backsound.wav");

        // Set background color
        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(20, 20));

        // Panel for title without gradient background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(500, 150));
        titlePanel.setOpaque(false); // Make the panel transparent

        // Load your image
        ImageIcon originalIcon = new ImageIcon("src/Sudoku/desainsudoku1.png");
        originalImage = originalIcon.getImage();

        // Create a JLabel with the image
        lblWelcome = new JLabel();
        lblWelcome.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(lblWelcome, BorderLayout.NORTH); // Align image to the top

        // Add a component listener to resize the image when the panel size changes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(titlePanel.getWidth(), titlePanel.getHeight());
            }
        });

        // Main menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(backgroundColor);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Player name input
        JLabel nameLabel = new JLabel("Enter Your Name:");
        nameLabel.setFont(new Font("Fredoka One", Font.BOLD, 16)); // Use Fredoka One for title
        nameLabel.setForeground(textColor); // Set text color to white
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerNameField = new JTextField();
        playerNameField.setFont(new Font("Poppins", Font.PLAIN, 16)); // Use Poppins for input
        playerNameField.setMaximumSize(new Dimension(300, 40));
        playerNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(nameLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(playerNameField);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnPlayGame = createStyledButton("Play Game");
        JButton btnOptions = createStyledButton("Options");
        JButton btnExit = createStyledButton("Exit");

        // Add spacing between buttons
        addButtonToPanel(menuPanel, btnPlayGame);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addButtonToPanel(menuPanel, btnOptions);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addButtonToPanel(menuPanel, btnExit);

        // Action listeners
        btnPlayGame.addActionListener(e -> showDifficultySelection());
        btnOptions.addActionListener(e -> showOptions());
        btnExit.addActionListener(e -> System.exit(0));

        add(titlePanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void resizeImage(int panelWidth, int panelHeight) {
        if (originalImage == null) {
            return;
        }

        // Calculate the aspect ratio of the original image
        double imageAspectRatio = (double) originalImage.getWidth(null) / originalImage.getHeight(null);
        double panelAspectRatio = (double) panelWidth / panelHeight;

        int newWidth, newHeight;

        // Determine the scaling factor to fill the panel
        if (panelAspectRatio > imageAspectRatio) {
            // Panel is wider than the image
            newWidth = panelWidth;
            newHeight = (int) (panelWidth / imageAspectRatio);
        } else {
            // Panel is taller than the image
            newHeight = panelHeight;
            newWidth = (int) (panelHeight * imageAspectRatio);
        }

        // Scale the image
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        lblWelcome.setIcon(new ImageIcon(scaledImage));
    }

    private void playBackgroundMusic(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
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

    private void showOptions() {
        JDialog optionsDialog = new JDialog(this, "Options", true);
        optionsDialog.setLayout(new BorderLayout(10, 10));
        optionsDialog.setSize(400, 200);
        optionsDialog.getContentPane().setBackground(backgroundColor);

        JPanel volumePanel = new JPanel(new BorderLayout(10, 10));
        volumePanel.setBackground(backgroundColor);
        volumePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel volumeLabel = new JLabel("Volume Control");
        volumeLabel.setFont(new Font("Poppins", Font.BOLD, 16)); // Use Poppins for labels
        volumeLabel.setForeground(textColor);

        volumeSlider = new JSlider(0, 100, (int)(VolumeManager.getInstance().getVolume() * 100));
        volumeSlider.setBackground(backgroundColor);
        volumeSlider.setForeground(new Color(41, 128, 185));

        volumeSlider.addChangeListener(e -> {
            float newVolume = volumeSlider.getValue() / 100.0f;
            VolumeManager.getInstance().setVolume(newVolume);
        });

        JButton btnClose = createStyledButton("Save");
        btnClose.addActionListener(e -> optionsDialog.dispose());

        volumePanel.add(volumeLabel, BorderLayout.NORTH);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.add(btnClose, BorderLayout.SOUTH);

        optionsDialog.add(volumePanel);
        optionsDialog.setLocationRelativeTo(this);
        optionsDialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Open Sans", Font.BOLD, 18)); // Use Open Sans for buttons
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185)); // Primary color
        button.setPreferredSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219)); // Secondary color
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Add border on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185)); // Primary color
                button.setBorder(null); // Remove border when not hovered
            }
        });

        return button;
    }

    private void addButtonToPanel(JPanel panel, JButton button) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(400, 70));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(button);
        panel.add(buttonPanel);
    }

    private void showDifficultySelection() {
        JDialog difficultyDialog = new JDialog(this, "Select Difficulty", true);
        difficultyDialog.setLayout(new BorderLayout());
        difficultyDialog.setSize(400, 300);
        difficultyDialog.getContentPane().setBackground(backgroundColor);

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));
        difficultyPanel.setBackground(backgroundColor);
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnEasy = createStyledButton("Easy");
        JButton btnMedium = createStyledButton("Medium");
        JButton btnHard = createStyledButton("Hard");

        addButtonToPanel(difficultyPanel, btnEasy);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        addButtonToPanel(difficultyPanel, btnMedium);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        addButtonToPanel(difficultyPanel, btnHard);

        btnEasy.addActionListener(e -> {
            startGame(SudokuConstants.EASY);
            difficultyDialog.dispose();
        });
        btnMedium.addActionListener(e -> {
            startGame(SudokuConstants.MEDIUM);
            difficultyDialog.dispose();
        });
        btnHard.addActionListener(e -> {
            startGame(SudokuConstants.HARD);
            difficultyDialog.dispose();
        });

        difficultyDialog.add(difficultyPanel, BorderLayout.CENTER);
        difficultyDialog.setLocationRelativeTo(this);
        difficultyDialog.setVisible(true);
    }

    private void startGame(int difficulty) {
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name before starting the game.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Sudoku(difficulty, playerName);
        dispose();
    }

    @Override
    public void dispose() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen());
    }
}