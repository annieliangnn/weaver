package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Collections;

/**
 * The Model handles game logic and state for the Weaver word game.
 * It loads a dictionary of valid words, tracks the start and target words,
 * maintains guess history, and notifies observers of changes.
 */
public class Model extends Observable {
    private List<String> dictionary;        // All valid 4-letter words
    private String startWord;               // Starting word
    private String targetWord;              // Goal word
    private String currentInput;            // Most recent guess
    private List<String> historyWords;      // List of all guesses
    private GameState gameState;            // Current game status
    private boolean showErrorFlag;          // Whether to display invalid-word errors
    private boolean showPathFlag;           // Whether to display solution path
    private boolean randomWordFlag;         // Whether to pick start/target at random
    /**
     * Constructor initializes dictionaries and game state.
     */
    public Model() {
        dictionary = new ArrayList<>();
        historyWords = new ArrayList<>();
        gameState = GameState.IN_PROGRESS;
    }
    /**
     * Possible game outcomes.
     */
    public enum GameState {
        IN_PROGRESS, WIN, LOSE
    }
    /**
     * Loads the dictionary from a fixed file path.
     * Only four-letter words are added.
     */
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
    /**
     * Sets the start and target words either randomly or as given.
     * Notifies observers of the new pair.
     */
    public void setStartAndTargetWords(String start, String target) {
        if (randomWordFlag) {
            // Random word selection
            if (!dictionary.isEmpty()) {
                int randomIndex1 = (int) (Math.random() * dictionary.size());
                int randomIndex2;
                do {
                    randomIndex2 = (int) (Math.random() * dictionary.size());
                } while (randomIndex2 == randomIndex1);
                startWord = dictionary.get(randomIndex1);
                targetWord = dictionary.get(randomIndex2);
            }
        } else {
            // Use fixed words
            startWord = start;
            targetWord = target;
        }
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, null});
    }
    /**
     * Checks that the given word is in the dictionary and has correct length.
     */
    public boolean isValidWord(String word) {
        return dictionary.contains(word) && word.length() == startWord.length();
    }
    /**
     * Counts differing character positions between two equal-length words.
     * Returns -1 if words are null or lengths differ.
     */
    public int countLetterDifferences(String word1, String word2) {
        if (word1 == null || word2 == null || word1.length() != word2.length()) {
            return -1;
        }
        int differences = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                differences++;
            }
        }
        return differences;
    }
    /**
     * Processes a new guess. Validates dictionary membership and one-letter change.
     * Notifies observers of errors or successful addition.
     */
    public void updateCurrentInput(String input) {
        if (input != null) {
            // Check dictionary membership
            if (!isValidWord(input)) {
                setChanged();
                notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, "Invalid Word"});
                return;
            }
            // Check if it is the first input
            if (historyWords.isEmpty()) {
                if (countLetterDifferences(input, startWord) > 1) {
                    setChanged();
                    notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, "Only one letter can be changed at a time！"});
                    return;
                }
            } else {
                // Check the differences from the previous input word
                String lastWord = historyWords.get(historyWords.size() - 1);
                if (lastWord != null && countLetterDifferences(input, lastWord) > 1) {
                    setChanged();
                    notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, "Only one letter can be changed at a time！"});
                    return;
                }
            }
        }

        currentInput = input;
        historyWords.add(currentInput);
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, null});
    }
    /**
     * Removes the last guess from history and notifies observers.
     */
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

    /**
     * Checks if the most recent guess matches the target. Updates state.
     */
    public boolean checkWin() {
        if (currentInput != null && currentInput.equals(targetWord)) {
            gameState = GameState.WIN;
            setChanged();
            notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
            return true;
        }
        return false;
    }


    public String getStartWord() {
        return startWord;
    }

    public String getTargetWord() {
        return targetWord;
    }
    public GameState getGameState() {
        return gameState;
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
    /**
     * Resets the game state and clears history.
     */
    public void resetGame() {
        currentInput = null;
        historyWords.clear();
        gameState = GameState.IN_PROGRESS;
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords});
    }

    /**
     * Finds the minimum transformation path between start and target words.
     * Uses a BFS over the dictionary graph.
     */
    public List<String> getSolutionPath() {
        if (startWord == null || targetWord == null) return null;
        if (startWord.equals(targetWord))
            return Collections.singletonList(startWord);


        Set<String> dictSet = new HashSet<>(dictionary);
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(Collections.singletonList(startWord));
        visited.add(startWord);

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String last = path.get(path.size() - 1);
            for (String next : getOneLetterDiffWords(last, dictSet)) {
                if (visited.contains(next)) continue;
                List<String> newPath = new ArrayList<>(path);
                newPath.add(next);
                if (next.equals(targetWord)) {
                    return newPath;
                }
                queue.offer(newPath);
                visited.add(next);
            }
        }
        return null; // no transformation path found
    }
    /**
     * Helper: returns all dictionary words differing by one letter.
     */

    private List<String> getOneLetterDiffWords(String word, Set<String> dictSet) {
        List<String> result = new ArrayList<>();
        char[] arr = word.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char original = arr[i];
            for (char c = 'a'; c <= 'z'; c++) {
                if (c == original) continue;
                arr[i] = c;
                String newWord = new String(arr);
                if (dictSet.contains(newWord)) {
                    result.add(newWord);
                }
            }
            arr[i] = original;
        }
        return result;
    }
    // add getDictionary method for test
    public List<String> getDictionary() {
        return dictionary;
    }

}