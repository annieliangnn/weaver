package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Observable;

public class Model extends Observable {
    private List<String> dictionary;
    private String startWord;
    private String targetWord;
    private String currentInput;
    private List<String> historyWords;
    private GameState gameState;
    private boolean showErrorFlag;
    private boolean showPathFlag;
    private boolean randomWordFlag;

    public Model() {
        dictionary = new ArrayList<>();
        historyWords = new ArrayList<>();
        gameState = GameState.IN_PROGRESS;
    }

    public enum GameState {
        IN_PROGRESS, WIN, LOSE
    }

    public void loadDictionary() {
        try {
            File file = new File("D:\\weaver1\\resources\\dictionary.txt"); // 从根目录加载字典文件
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim();
                if (word.length() == 4) { // 只添加4字母的单词
                    dictionary.add(word);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("字典文件未找到！");
        }
    }

    public void setStartAndTargetWords(String start, String target) {
        if (!randomWordFlag) {
            startWord = start;
            targetWord = target;
        } else {
            if (!dictionary.isEmpty()) {
                int randomIndex1 = (int) (Math.random() * dictionary.size());
                int randomIndex2;
                do {
                    randomIndex2 = (int) (Math.random() * dictionary.size());
                } while (randomIndex2 == randomIndex1);
                startWord = dictionary.get(randomIndex1);
                targetWord = dictionary.get(randomIndex2);
            }
        }
    }

    public boolean isValidWord(String word) {
        return dictionary.contains(word) && word.length() == startWord.length();
    }

    public void updateCurrentInput(String input) {
        currentInput = input;
        historyWords.add(currentInput);
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
    }

    public void removeLastInput() {
        if (!historyWords.isEmpty()) {
            historyWords.remove(historyWords.size() - 1); // 移除最后一个单词
        }
        currentInput = null; // 重置当前输入
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
    }

    public boolean checkWin() {
        if (currentInput != null && currentInput.equals(targetWord)) {
            gameState = GameState.WIN;
            setChanged();
            notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
            return true;
        }
        return false;
    }

    public String getErrorMessage() {
        if (showErrorFlag && currentInput != null && !isValidWord(currentInput)) {
            return "无效的单词！";
        }
        return null;
    }

    public List<String> getPath() {
        if (showPathFlag) {
            List<String> path = new ArrayList<>();
            path.add(startWord);
            path.add(targetWord);
            return path;
        }
        return null;
    }

    public String getStartWord() {
        return startWord;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public String getCurrentInput() {
        return currentInput;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setShowErrorFlag(boolean showErrorFlag) {
        this.showErrorFlag = showErrorFlag;
    }

    public void setShowPathFlag(boolean showPathFlag) {
        this.showPathFlag = showPathFlag;
    }

    public void setRandomWordFlag(boolean randomWordFlag) {
        this.randomWordFlag = randomWordFlag;
    }

    public void resetGame() {
        currentInput = null;
        historyWords.clear();
        gameState = GameState.IN_PROGRESS;
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
    }
}