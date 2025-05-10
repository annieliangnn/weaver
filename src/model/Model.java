package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Model extends Observable {
    private static List<String> list;
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
            File file = new File("D:\\AOOP\\cw\\weaver\\out\\production\\weaver1\\dictionary.txt"); //load the dictionary.txt file
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim();
                if (word.length() == 4) { // Only add four-letter words
                    dictionary.add(word);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Dictionary file not found！");
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
            historyWords.remove(historyWords.size() - 1); // remove the last word
        }
        currentInput = null; // reset current input
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
    }

    public List<String> getHistoryWords() {
        return historyWords;
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
            return "Invalid Word！";
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

    // add getDictionary method for test
    public List<String> getDictionary() {
        return dictionary;
    }

}