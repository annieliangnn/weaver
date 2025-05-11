package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Collections;


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

    public boolean isValidWord(String word) {
        return dictionary.contains(word) && word.length() == startWord.length();
    }

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

    public void updateCurrentInput(String input) {
        if (input != null) {
            // 检查是否是合法单词
            if (!isValidWord(input)) {
                setChanged();
                notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, "单词不符合要求"});
                return;
            }
            // 检查是否是第一个输入
            if (historyWords.isEmpty()) {
                if (countLetterDifferences(input, startWord) > 1) {
                    setChanged();
                    notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, "一次只能改变一个字母！"});
                    return;
                }
            } else {
                // 检查与上一个输入单词的差异
                String lastWord = historyWords.get(historyWords.size() - 1);
                if (lastWord != null && countLetterDifferences(input, lastWord) > 1) {
                    setChanged();
                    notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, "一次只能改变一个字母！"});
                    return;
                }
            }
        }

        currentInput = input;
        historyWords.add(currentInput);
        setChanged();
        notifyObservers(new Object[]{gameState, startWord, targetWord, historyWords, null});
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
    // 添加 getDictionary 方法用于测试


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
        return null; // 没有路径
    }
    // 辅助方法：找出所有只差一个字母的有效单词
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