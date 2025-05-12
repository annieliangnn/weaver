package view;

import model.Model;
import model.Model.GameState;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // Information Panel
        JPanel infoPanel = createInfoPanel();

        // Game History Panel
        gamePanel = createGamePanel();

        // Scroll pane for history with fixed size
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Checkbox panel
        JPanel checkboxPanel = createCheckboxPanel();

        // Bottom combination panel
        JPanel southPanel = createSouthPanel();

        // Layout management
        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(checkboxPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        // Initialize input and update display
        clearInputPanel();
        update(model, new Object[]{
                model.getGameState(),
                model.getStartWord(),
                model.getTargetWord(),
                model.getHistoryWords(),
                null
        });

        // Pack to respect preferred sizes
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(30, 30, 36));

        startWordLabel = new JLabel("Start Word: ");
        targetWordLabel = new JLabel("End Word: ");
        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);

        startWordLabel.setFont(labelFont);
        startWordLabel.setForeground(Color.WHITE);
        targetWordLabel.setFont(labelFont);
        targetWordLabel.setForeground(Color.WHITE);

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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(200, 200, 200));

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Input panel with fixed height
        inputPanel = createInputPanel();
        inputPanel.setPreferredSize(new Dimension(700, 80));

        // Keyboard panel with fixed height
        keyboardPanel = createKeyboardPanel();
        keyboardPanel.setPreferredSize(new Dimension(700, 200));

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
        newGameButton.setEnabled(false);

        resetButton.addActionListener(e -> {
            model.removeLastInput();
            clearInputPanel();
        });

        newGameButton.addActionListener(e -> {
            model.setRandomWordFlag(true);
            model.setStartAndTargetWords(null, null);
            model.setRandomWordFlag(false);
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
                {"Q","W","E","R","T","Y","U","I","O","P"},
                {"A","S","D","F","G","H","J","K","L"},
                {"Z","X","C","V","B","N","M"}
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

        // Delete key
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
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        delButton.addActionListener(e -> {
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
                BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        button.addActionListener(e -> addLetterToInput(text.toLowerCase()));
        return button;
    }

    private void addLetterToInput(String letter) {
        // Fill next empty input box
        for (Component comp : inputPanel.getComponents()) {
            JTextField field = (JTextField) comp;
            if (field.getText().isEmpty()) {
                field.setText(letter);
                break;
            }
        }
        // Build the current word
        StringBuilder sb = new StringBuilder();
        for (Component comp : inputPanel.getComponents()) {
            sb.append(((JTextField) comp).getText());
        }
        if (sb.length() == 4) {
            String word = sb.toString();
            // Check dictionary membership first
            if (!model.isValidWord(word)) {
                JOptionPane.showMessageDialog(this,
                        "Word not in dictionary!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                clearInputPanel();
                return;
            }
            // validate one-letter difference
            List<String> history = model.getHistoryWords();
            String compareTo = history.isEmpty() ? model.getStartWord() : history.get(history.size() - 1);
            if (model.countLetterDifferences(word, compareTo) > 1) {
                JOptionPane.showMessageDialog(this,
                        "You can only change one letter at a time!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                clearInputPanel();
                return;
            }
            // Valid move: update model and check win
            model.updateCurrentInput(word);
            if (model.checkWin()) {
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You Win!", "Victory",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            clearInputPanel();
            // no extra updateCurrentInput(null)
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
                    BorderFactory.createLineBorder(new Color(150,150,150)),
                    BorderFactory.createEmptyBorder(5,5,5,5)
            ));
            inputPanel.add(field);
        }
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(java.util.Observable o, Object arg) {
        if (!(o instanceof Model) || arg == null) return;
        Object[] state = (Object[]) arg;
        GameState gameState = (GameState) state[0];
        String startWord = (String) state[1];
        String targetWord = (String) state[2];
        @SuppressWarnings("unchecked")
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
            if (word != null) addWordToGamePanel(word.toUpperCase(), targetWord.toUpperCase());
        }
        if (gameState == GameState.WIN) {
            // Already shown in addLetterToInput
        }
        resetButton.setEnabled(gameState == GameState.IN_PROGRESS);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private void addWordToGamePanel(String word, String targetWord) {
        JPanel wordPanel = new JPanel(new GridLayout(1,4,5,5));
        wordPanel.setBackground(new Color(245,245,245));
        for (int i=0; i<4; i++) {
            JTextField field = new JTextField();
            field.setEditable(false);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setFont(new Font("Consolas", Font.BOLD, 24));
            field.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            char c = word.charAt(i);
            char t = targetWord.charAt(i);
            if (showErrorsCheckBox.isSelected()) {
                if (c==t) field.setBackground(new Color(144,238,144));
                else if (targetWord.indexOf(c)!=-1) field.setBackground(new Color(255,255,153));
                else field.setBackground(new Color(211,211,211));
            } else {
                field.setBackground(Color.WHITE);
            }
            field.setText(String.valueOf(c));
            wordPanel.add(field);
        }
        gamePanel.add(wordPanel);
    }

    private JPanel createCheckboxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30,30,36));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        showErrorsCheckBox = new JCheckBox("Show Errors", true);
        showErrorsCheckBox.setForeground(Color.WHITE);
        showErrorsCheckBox.setBackground(new Color(30,30,36));
        showErrorsCheckBox.addActionListener(e -> {
            showErrorsFlag = showErrorsCheckBox.isSelected();
            model.setShowErrorFlag(showErrorsFlag);
            update(model, new Object[]{model.getGameState(), model.getStartWord(), model.getTargetWord(), model.getHistoryWords(), null});
        });

        showSolutionPathCheckBox = new JCheckBox("Show Solution Path", true);
        showSolutionPathCheckBox.setForeground(Color.WHITE);
        showSolutionPathCheckBox.setBackground(new Color(30,30,36));
        showSolutionPathCheckBox.addActionListener(e -> {
            showSolutionPathFlag = showSolutionPathCheckBox.isSelected();
            model.setShowPathFlag(showSolutionPathFlag);
            if (showSolutionPathFlag) {
                List<String> path = model.getSolutionPath();
                JOptionPane.showMessageDialog(this, path != null? String.join(" → ", path): "No valid path found!", "Hint", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        randomGameCheckBox = new JCheckBox("Random Game", false);
        randomGameCheckBox.setForeground(Color.WHITE);
        randomGameCheckBox.setBackground(new Color(30,30,36));
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
        SwingUtilities.invokeLater(() -> new GUIView(model).setVisible(true));
    }
}
