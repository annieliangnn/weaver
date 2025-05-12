package view;

import model.Model;
import model.Model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class GUIView extends JFrame implements java.util.Observer {
    private final Model model;

    private final JButton resetButton;
    private final JButton newGameButton;
    private final JPanel gamePanel;
    private final JPanel inputPanel;
    private final JPanel keyboardPanel;
    private final JLabel startWordLabel;
    private final JLabel targetWordLabel;

    private final JCheckBox showErrorsCheckBox;
    private final JCheckBox showSolutionPathCheckBox;
    private final JCheckBox randomGameCheckBox;
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

        // Top info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        infoPanel.setBackground(new Color(30, 30, 36));
        startWordLabel  = new JLabel("Start Word: ");
        targetWordLabel = new JLabel("Target Word: ");
        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        startWordLabel.setFont(labelFont);
        startWordLabel.setForeground(Color.WHITE);
        targetWordLabel.setFont(labelFont);
        targetWordLabel.setForeground(Color.WHITE);
        infoPanel.add(startWordLabel);
        infoPanel.add(targetWordLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Center history panel
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBackground(new Color(245, 245, 245));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel: buttons, input, keyboard
        JPanel south = new JPanel(new BorderLayout(10, 10));
        south.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        south.setBackground(new Color(200, 200, 200));

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(64, 64, 64));
        resetButton   = new JButton("Reset");
        newGameButton = new JButton("New Game");
        newGameButton.setEnabled(false);
        resetButton.addActionListener(e -> {
            model.removeLastInput();
            clearInputPanel();
        });
        newGameButton.addActionListener(e -> {
            model.setRandomWordFlag(randomGameFlag);
            model.setStartAndTargetWords(null, null);
            model.resetGame();
            clearInputPanel();
        });
        btnPanel.add(resetButton);
        btnPanel.add(newGameButton);
        south.add(btnPanel, BorderLayout.NORTH);

        // Input panel
        inputPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        inputPanel.setPreferredSize(new Dimension(800, 60));
        south.add(inputPanel, BorderLayout.CENTER);

        // Keyboard panel
        keyboardPanel = buildKeyboardPanel();
        keyboardPanel.setPreferredSize(new Dimension(800, 200));
        south.add(keyboardPanel, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        // Right‐side controls
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(30, 30, 36));
        side.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        Dimension cbSize = new Dimension(160, 50);
        showErrorsCheckBox = new JCheckBox("Show Errors", true);
        showSolutionPathCheckBox = new JCheckBox("Show Path", true);
        randomGameCheckBox = new JCheckBox("Random Game", false);

        for (JCheckBox cb : new JCheckBox[]{showErrorsCheckBox, showSolutionPathCheckBox, randomGameCheckBox}) {
            cb.setPreferredSize(cbSize);
            cb.setMaximumSize(cbSize);
            cb.setForeground(Color.WHITE);
            cb.setBackground(new Color(30, 30, 36));
            side.add(cb);
            side.add(Box.createVerticalStrut(10));
        }

        showErrorsCheckBox.addActionListener(e -> {
            showErrorsFlag = showErrorsCheckBox.isSelected();
            model.setShowErrorFlag(showErrorsFlag);
            refreshHistory();
        });

        showSolutionPathCheckBox.addActionListener(e -> {
            showSolutionPathFlag = showSolutionPathCheckBox.isSelected();
            model.setShowPathFlag(showSolutionPathFlag);
            if (showSolutionPathFlag) {
                List<String> path = model.getSolutionPath();
                JOptionPane.showMessageDialog(this,
                        path != null ? String.join(" → ", path) : "No valid path found!",
                        "Hint", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        randomGameCheckBox.addActionListener(e -> {
            randomGameFlag = randomGameCheckBox.isSelected();
            newGameButton.setEnabled(randomGameFlag);
        });

        add(side, BorderLayout.EAST);

        // Init
        clearInputPanel();
        update(model, new Object[]{
                model.getGameState(),
                model.getStartWord(),
                model.getTargetWord(),
                model.getHistoryWords(),
                null
        });

        // Enable physical keyboard
        setupKeyBindings();

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildKeyboardPanel() {
        JPanel p = new JPanel();
        p.setBackground(new Color(30, 30, 36));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        String[][] rows = {
                {"Q","W","E","R","T","Y","U","I","O","P"},
                {"A","S","D","F","G","H","J","K","L"},
                {"Z","X","C","V","B","N","M"}
        };
        for (String[] row : rows) {
            JPanel r = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            r.setBackground(new Color(30, 30, 36));
            for (String k : row) {
                JButton b = new JButton(k);
                b.setPreferredSize(new Dimension(50, 40));
                b.addActionListener(e -> addLetterToInput(k.toLowerCase()));
                r.add(b);
            }
            p.add(r);
        }
        JPanel delRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        delRow.setBackground(new Color(30, 30, 36));
        JButton del = new JButton("DEL");
        del.setPreferredSize(new Dimension(60, 40));
        del.setBackground(Color.RED);
        del.setForeground(Color.WHITE);
        del.addActionListener(e -> {
            for (int i = inputPanel.getComponentCount() - 1; i >= 0; i--) {
                JTextField f = (JTextField)inputPanel.getComponent(i);
                if (!f.getText().isEmpty()) { f.setText(""); break; }
            }
        });
        delRow.add(del);
        p.add(delRow);
        return p;
    }

    private void addLetterToInput(String letter) {
        for (Component c : inputPanel.getComponents()) {
            JTextField f = (JTextField)c;
            if (f.getText().isEmpty()) { f.setText(letter); break; }
        }
        StringBuilder sb = new StringBuilder();
        for (Component c : inputPanel.getComponents()) sb.append(((JTextField)c).getText());
        if (sb.length() == 4) {
            String w = sb.toString();
            if (!model.isValidWord(w)) {
                JOptionPane.showMessageDialog(this, "Word not in dictionary!", "Error", JOptionPane.ERROR_MESSAGE);
                clearInputPanel();
                return;
            }
            List<String> hist = model.getHistoryWords();
            String prev = hist.isEmpty() ? model.getStartWord() : hist.get(hist.size() - 1);
            if (model.countLetterDifferences(w, prev) > 1) {
                JOptionPane.showMessageDialog(this, "Only one letter change allowed!", "Error", JOptionPane.ERROR_MESSAGE);
                clearInputPanel();
                return;
            }
            model.updateCurrentInput(w);
            if (model.checkWin()) {
                JOptionPane.showMessageDialog(this, "Congratulations! You Win!", "Victory", JOptionPane.INFORMATION_MESSAGE);
            }
            clearInputPanel();
        }
    }

    private void clearInputPanel() {
        inputPanel.removeAll();
        for (int i = 0; i < 4; i++) {
            JTextField f = new JTextField();
            f.setEditable(false);
            f.setFont(new Font("Consolas", Font.BOLD, 24));
            f.setHorizontalAlignment(JTextField.CENTER);
            inputPanel.add(f);
        }
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(java.util.Observable o, Object arg) {
        if (!(o instanceof Model) || arg == null) return;
        Object[] s = (Object[])arg;
        GameState gs = (GameState)s[0];
        String st = (String)s[1], tg = (String)s[2];
        List<String> hist = (List<String>)s[3];
        startWordLabel.setText("Start Word: " + st.toUpperCase());
        targetWordLabel.setText("Target Word: " + tg.toUpperCase());
        gamePanel.removeAll();
        for (String w : hist) {
            if (w == null) continue;
            JPanel row = new JPanel(new GridLayout(1,4,5,5));
            row.setBackground(new Color(245,245,245));
            for (int i = 0; i < 4; i++) {
                JTextField f = new JTextField(String.valueOf(w.charAt(i)));
                f.setEditable(false);
                f.setHorizontalAlignment(JTextField.CENTER);
                f.setFont(new Font("Consolas", Font.BOLD, 24));
                if (showErrorsFlag) {
                    char c = w.charAt(i), t = tg.charAt(i);
                    if (c == t)            f.setBackground(new Color(144,238,144));
                    else if (tg.indexOf(c) >= 0) f.setBackground(new Color(255,255,153));
                    else                      f.setBackground(new Color(211,211,211));
                }
                row.add(f);
            }
            gamePanel.add(row);
        }
        resetButton.setEnabled(gs == GameState.IN_PROGRESS);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private void refreshHistory() {
        update(model, new Object[]{
                model.getGameState(),
                model.getStartWord(),
                model.getTargetWord(),
                model.getHistoryWords(),
                null
        });
    }

    private void setupKeyBindings() {
        JComponent root = (JComponent)getContentPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // A-Z keys
        for (char c = 'A'; c <= 'Z'; c++) {
            String name = "letter_" + c;
            im.put(KeyStroke.getKeyStroke(c), name);
            im.put(KeyStroke.getKeyStroke(Character.toLowerCase(c)), name);
            am.put(name, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addLetterToInput(name.substring(name.length()-1).toLowerCase());
                }
            });
        }

        // ENTER
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
        am.put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // trigger same logic as if 4th letter was pressed
                StringBuilder sb = new StringBuilder();
                for (Component c : inputPanel.getComponents()) {
                    sb.append(((JTextField)c).getText());
                }
                if (sb.length() == 4) {
                    addLetterToInput("");
                }
            }
        });

        // BACKSPACE
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");
        am.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = inputPanel.getComponentCount()-1; i>=0; i--) {
                    JTextField f = (JTextField)inputPanel.getComponent(i);
                    if (!f.getText().isEmpty()) { f.setText(""); break; }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIView(new Model()).setVisible(true));
    }
}
