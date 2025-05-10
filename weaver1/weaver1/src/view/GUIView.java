package view;

import model.Model;
import model.Model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建消息显示区域（显示起始词和目标词）
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(Color.LIGHT_GRAY);

        startWordLabel = new JLabel();
        startWordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        targetWordLabel = new JLabel();
        targetWordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(startWordLabel);
        infoPanel.add(targetWordLabel);

        // 创建游戏面板（显示单词输入框）
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(0, 1)); // 垂直布局，自动增加行
        gamePanel.setBackground(Color.WHITE);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建输入面板
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 4));
        inputPanel.setPreferredSize(new Dimension(400, 50));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setPreferredSize(new Dimension(100, 40));
        resetButton.setBackground(Color.ORANGE);
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.setPreferredSize(new Dimension(120, 40));
        newGameButton.setBackground(Color.ORANGE);
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        resetButton.setEnabled(true);
        buttonPanel.add(resetButton);
        buttonPanel.add(newGameButton);

        // 创建键盘面板
        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 10, 5, 5));
        keyboardPanel.setBackground(Color.LIGHT_GRAY);
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] letters = "abcdefghijklmnopqrstuvwxyz".split("");
        for (String letter : letters) {
            JButton button = new JButton(letter);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setPreferredSize(new Dimension(40, 40));
            button.setBackground(Color.CYAN);
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addLetterToInput(letter);
                }
            });
            keyboardPanel.add(button);
        }

        // 布局管理
        add(infoPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);
        add(keyboardPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeLastInput(); // 清空当前输入
                clearInputPanel(); // 清空输入面板
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRandomWordFlag(true);
                model.setStartAndTargetWords(null, null);
                model.updateCurrentInput(null);
                model.resetGame();
                clearInputPanel();
            }
        });

        // 初始化输入面板
        clearInputPanel();
    }

    private void addLetterToInput(String letter) {
        for (int i = 0; i < inputPanel.getComponentCount(); i++) {
            JTextField field = (JTextField) inputPanel.getComponent(i);
            if (field.getText().isEmpty()) {
                field.setText(letter);
                break;
            }
        }

        boolean isFull = true;
        for (int i = 0; i < inputPanel.getComponentCount(); i++) {
            JTextField field = (JTextField) inputPanel.getComponent(i);
            if (field.getText().isEmpty()) {
                isFull = false;
                break;
            }
        }

        if (isFull) {
            String inputWord = "";
            for (int i = 0; i < inputPanel.getComponentCount(); i++) {
                JTextField field = (JTextField) inputPanel.getComponent(i);
                inputWord += field.getText();
            }
            model.updateCurrentInput(inputWord);
            if (model.checkWin()) {
                JOptionPane.showMessageDialog(null, "You win!");
            }
            clearInputPanel();
            model.updateCurrentInput(null); // 清空 currentInput
        }
    }

    private void clearInputPanel() {
        inputPanel.removeAll();
        for (int i = 0; i < 4; i++) {
            JTextField field = new JTextField();
            field.setEditable(false);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setPreferredSize(new Dimension(50, 50));
            field.setFont(new Font("Arial", Font.BOLD, 16));
            field.setBackground(Color.WHITE);
            field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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

            startWordLabel.setText("Start Word: " + startWord);
            targetWordLabel.setText("Target Word: " + targetWord);

            gamePanel.removeAll();
            for (String word : historyWords) {
                if (word != null) {
                    addWordToGamePanel(word, targetWord);
                }
            }

            if (gameState == GameState.WIN) {
                JOptionPane.showMessageDialog(null, "You win!");
            }

            // 根据游戏状态启用 Reset 按钮
            resetButton.setEnabled(gameState == GameState.IN_PROGRESS);
        }
    }

    private void addWordToGamePanel(String word, String targetWord) {
        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new GridLayout(1, 4));
        wordPanel.setBackground(Color.WHITE);
        wordPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        for (int i = 0; i < 4; i++) {
            JTextField field = new JTextField();
            field.setEditable(false);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setPreferredSize(new Dimension(50, 50));
            field.setFont(new Font("Arial", Font.BOLD, 16));
            char c = word.charAt(i);
            if (i < targetWord.length() && c == targetWord.charAt(i)) {
                field.setText(String.valueOf(c));
                field.setBackground(Color.GREEN);
            } else if (targetWord.indexOf(c) != -1) {
                field.setText(String.valueOf(c));
                field.setBackground(Color.YELLOW);
            } else {
                field.setText(String.valueOf(c));
                field.setBackground(Color.GRAY);
            }
            wordPanel.add(field);
        }
        gamePanel.add(wordPanel);
        gamePanel.revalidate();
        gamePanel.repaint();
    }
}