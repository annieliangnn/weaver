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

public class GUIView extends JFrame implements java.util.Observer {
    private Model model;
    private JButton resetButton;
    private JButton newGameButton;
    private JPanel gamePanel;
    private JPanel inputPanel;
    private JLabel startWordLabel;
    private JLabel targetWordLabel;
    private JPanel keyboardPanel;

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

        // layout management
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(gamePanel), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Initialize the input panel
        clearInputPanel();
        setupKeyBindings();
    }
        /**
         * Use Swing Key Bindings to handle keyboard input when any child component gains focus
         */
        private void setupKeyBindings() {
            JComponent root = (JComponent) getContentPane();
            InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = root.getActionMap();

            // Bind the A-Z keys
            for (char c = 'A'; c <= 'Z'; c++) {
                String key = String.valueOf(c);
                im.put(KeyStroke.getKeyStroke(c), "letter_" + key);
                am.put("letter_" + key, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addLetterToInput(key.toLowerCase());
                    }
                });
            }
            //  Bind the a-z keys
            for (char c = 'a'; c <= 'z'; c++) {
                String key = String.valueOf(c);
                im.put(KeyStroke.getKeyStroke(c), "letter_" + key.toUpperCase());
                am.put("letter_" + key.toUpperCase(), new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addLetterToInput(key);
                    }
                });
            }
            // Press Enter to submit
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submitInput");
            am.put("submitInput", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    submitCurrentInput();
                }
            });
        }

        private void submitCurrentInput() {
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

        // 按钮面板
        JPanel buttonPanel = createButtonPanel();

        // 输入面板
        inputPanel = createInputPanel();

        // 键盘面板
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

        resetButton.addActionListener(e -> {
            model.removeLastInput();
            clearInputPanel();
        });

        newGameButton.addActionListener(e -> {
            model.setRandomWordFlag(true);
            model.setStartAndTargetWords(null, null);
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

            startWordLabel.setText("Start Word: " + startWord.toUpperCase());
            targetWordLabel.setText("End Word: " + targetWord.toUpperCase());

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

            if (c == targetChar) {
                field.setBackground(new Color(119, 239, 119)); // Correct position color
            } else if (targetWord.indexOf(c) != -1) {
                field.setBackground(new Color(248, 248, 89)); // It exists but is in the wrong position
            } else {
                field.setBackground(new Color(211, 211, 211)); //  no letter exists.
            }

            field.setText(String.valueOf(c));

            wordPanel.add(field);
        }

        gamePanel.add(wordPanel);
    }

    public static void main(String[] args) {
        Model model = new Model();
        SwingUtilities.invokeLater(() -> {
            GUIView view = new GUIView(model);
            view.setVisible(true);
        });
    }
}