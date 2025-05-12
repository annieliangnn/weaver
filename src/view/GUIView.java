package view;

import model.Model;
import model.Model.GameState;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Scanner;

public class GUIView extends JFrame implements java.util.Observer {
    private Model model;
    private JButton resetButton;
    private JButton newGameButton;
    private JPanel gamePanel;
    private JPanel inputPanel;
    private JLabel startWordLabel;
    private JLabel targetWordLabel;
    private JPanel keyboardPanel;
    private JCheckBox showErrorsCheckBox;
    private JCheckBox showSolutionPathCheckBox;
    private JCheckBox randomGameCheckBox;
    private boolean showErrorsFlag = true;
    private boolean showSolutionPathFlag = true;
    private boolean randomGameFlag = false;

    public GUIView(Model model) {
        this.model = model;
        model.addObserver(this);

        setTitle("Weaver Game");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 255, 255, 255)); // 深蓝灰色背景
        setLayout(new BorderLayout(10, 10));

        // Information Panel
        JPanel infoPanel = createInfoPanel();

        // Game History Panel
        gamePanel = createGamePanel();

        //Bottom combination panel
        JPanel southPanel = createSouthPanel();

        // Checkbox panel
        JPanel checkboxPanel = createCheckboxPanel();

        // layout management
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(gamePanel), BorderLayout.CENTER);
        add(checkboxPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        // Initialize the input panel
        clearInputPanel();
        //setupKeyBindings();
        // Key: manually refresh the interface once
        update(model, new Object[]{
                model.getGameState(),
                model.getStartWord(),
                model.getTargetWord(),
                model.getHistoryWords(),
                null
        });
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(30, 30, 36));

        startWordLabel = new JLabel("Start Word: ");
        targetWordLabel = new JLabel("End Word: ");
        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);

        startWordLabel.setFont(labelFont);
        startWordLabel.setForeground(new Color(255, 255, 255));
        targetWordLabel.setFont(labelFont);
        targetWordLabel.setForeground(new Color(255, 255, 255));

        panel.add(startWordLabel);
        panel.add(targetWordLabel);
        return panel;
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }

    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panel.setBackground(new Color(200, 200, 200));

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Input panel
        inputPanel = createInputPanel();

        // Keyboard panel
        keyboardPanel = createKeyboardPanel();

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(keyboardPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(64, 64, 64));

        resetButton = createStyledButton("Reset", new Color(246, 252, 150));
        newGameButton = createStyledButton("New Game", new Color(140, 196, 106));
        newGameButton.setEnabled(false); // Initially disabled

        resetButton.addActionListener(e -> {
            model.removeLastInput();
            clearInputPanel();
        });

        newGameButton.addActionListener(e -> {
            model.setRandomWordFlag(true); // Set random flag
            model.setStartAndTargetWords(null, null); // Trigger random selection
            model.setRandomWordFlag(false); // Reset flag
            model.updateCurrentInput(null);
            model.resetGame();
            clearInputPanel();
        });

        panel.add(resetButton);
        panel.add(newGameButton);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        return button;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBackground(new Color(200, 200, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        return panel;
    }

    private JPanel createKeyboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(64, 64, 64));

        String[][] keyboardRows = {
                {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
                {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
                {"Z", "X", "C", "V", "B", "N", "M"}
        };

        for (String[] row : keyboardRows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
            rowPanel.setBackground(new Color(30, 30, 36));

            for (String key : row) {
                JButton button = createKeyButton(key);
                rowPanel.add(button);
            }
            panel.add(rowPanel);
        }
        // New DEL button
        JPanel delPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        delPanel.setBackground(new Color(30, 30, 36));
        JButton delButton = new JButton("DEL");
        delButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        delButton.setPreferredSize(new Dimension(60, 50));
        delButton.setFocusPainted(false);
        delButton.setBackground(new Color(255, 102, 102));
        delButton.setForeground(Color.WHITE);
        delButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        delButton.addActionListener(e -> {
            // Delete the last non-empty letter from the input field
            for (int i = inputPanel.getComponentCount() - 1; i >= 0; i--) {
                JTextField field = (JTextField) inputPanel.getComponent(i);
                if (!field.getText().isEmpty()) {
                    field.setText("");
                    break;
                }
            }
        });
        delPanel.add(delButton);
        panel.add(delPanel);

        return panel;
    }

    private JButton createKeyButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(50, 50));
        button.setFocusPainted(false);
        button.setBackground(new Color(200, 200, 210));
        button.setForeground(new Color(30, 30, 36));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        button.addActionListener(e -> addLetterToInput(text.toLowerCase()));
        return button;
    }

    private void addLetterToInput(String letter) {
        for (Component comp : inputPanel.getComponents()) {
            JTextField field = (JTextField) comp;
            if (field.getText().isEmpty()) {
                field.setText(letter);
                break;
            }
        }

        StringBuilder inputWord = new StringBuilder();
        for (Component comp : inputPanel.getComponents()) {
            JTextField field = (JTextField) comp;
            inputWord.append(field.getText());
        }

        if (inputWord.length() == 4) {
            model.updateCurrentInput(inputWord.toString());
            if (model.checkWin()) {
                JOptionPane.showMessageDialog(this, "Congratulations! You Win!", "Victory",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            clearInputPanel();
            model.updateCurrentInput(null);
        }
    }

    private void clearInputPanel() {
        inputPanel.removeAll();
        for (int i = 0; i < 4; i++) {
            JTextField field = new JTextField();
            field.setEditable(false);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setFont(new Font("Consolas", Font.BOLD, 24));
            field.setBackground(Color.WHITE);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            inputPanel.add(field);
        }
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof Model && arg != null) {
            Object[] state = (Object[]) arg;
            GameState gameState = (GameState) state[0];
            String startWord = (String) state[1];
            String targetWord = (String) state[2];
            List<String> historyWords = (List<String>) state[3];
            String errorMessage = state.length > 4 ? (String) state[4] : null;

            if (errorMessage != null) {
                JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            startWordLabel.setText("Start Word: " + startWord.toUpperCase());
            targetWordLabel.setText("Target Word: " + targetWord.toUpperCase());

            gamePanel.removeAll();
            for (String word : historyWords) {
                if (word != null) {
                    addWordToGamePanel(word.toUpperCase(), targetWord.toUpperCase());
                }
            }

            if (gameState == GameState.WIN) {
                JOptionPane.showMessageDialog(this, "Congratulations! You Win!",
                        "Victory", JOptionPane.INFORMATION_MESSAGE);
            }

            resetButton.setEnabled(gameState == GameState.IN_PROGRESS);
            gamePanel.revalidate();
            gamePanel.repaint();
        }
    }

    private void addWordToGamePanel(String word, String targetWord) {
        JPanel wordPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        wordPanel.setBackground(new Color(245, 245, 245));

        for (int i = 0; i < 4; i++) {
            JTextField field = new JTextField();
            field.setEditable(false);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setFont(new Font("Consolas", Font.BOLD, 24));
            field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            char c = word.charAt(i);
            char targetChar = targetWord.charAt(i);

            if (showErrorsCheckBox.isSelected()) {
                if (c == targetChar) {
                    field.setBackground(new Color(144, 238, 144)); // Correct position color
                } else if (targetWord.indexOf(c) != -1) {
                    field.setBackground(new Color(255, 255, 153)); // Wrong position color
                } else {
                    field.setBackground(new Color(211, 211, 211)); // Letter not in word
                }
            } else {
                field.setBackground(Color.WHITE); // White background when not showing errors
            }

            field.setText(String.valueOf(c));
            wordPanel.add(field);
        }

        gamePanel.add(wordPanel);
    }

    private JPanel createCheckboxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 36));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        showErrorsCheckBox = new JCheckBox("Show Errors");
        showErrorsCheckBox.setSelected(true);
        showErrorsCheckBox.setForeground(Color.WHITE);
        showErrorsCheckBox.setBackground(new Color(30, 30, 36));
        showErrorsCheckBox.addActionListener(e -> {
            showErrorsFlag = showErrorsCheckBox.isSelected();
            model.setShowErrorFlag(showErrorsFlag);
            update(model, new Object[]{
                    model.getGameState(),
                    model.getStartWord(),
                    model.getTargetWord(),
                    model.getHistoryWords(),
                    null
            });
        });

        showSolutionPathCheckBox = new JCheckBox("Show Solution Path");
        showSolutionPathCheckBox.setSelected(true);
        showSolutionPathCheckBox.setForeground(Color.WHITE);
        showSolutionPathCheckBox.setBackground(new Color(30, 30, 36));
        showSolutionPathCheckBox.addActionListener(e -> {
            showSolutionPathFlag = showSolutionPathCheckBox.isSelected();
            model.setShowPathFlag(showSolutionPathFlag);
            if (showSolutionPathFlag) {
                List<String> path = model.getSolutionPath();
                if (path != null) {
                    JOptionPane.showMessageDialog(this, "Path: " + String.join(" → ", path), "Hint", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No valid path found!", "Hint", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        randomGameCheckBox = new JCheckBox("Random Game");
        randomGameCheckBox.setSelected(false);
        randomGameCheckBox.setForeground(Color.WHITE);
        randomGameCheckBox.setBackground(new Color(30, 30, 36));
        randomGameCheckBox.addActionListener(e -> {
            randomGameFlag = randomGameCheckBox.isSelected();
            newGameButton.setEnabled(randomGameFlag);
        });

        panel.add(showErrorsCheckBox);
        panel.add(showSolutionPathCheckBox);
        panel.add(randomGameCheckBox);
        return panel;
    }
    public static void main(String[] args) {
        Model model = new Model();
        SwingUtilities.invokeLater(() -> {
            GUIView view = new GUIView(model);
            view.setVisible(true);
        });
    }
}