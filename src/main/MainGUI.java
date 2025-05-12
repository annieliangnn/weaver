package main;

import model.Model;
import view.GUIView;

public class MainGUI {
    public static void main(String[] args) {
        Model model = new Model();
        model.loadDictionary(); // Load the dictionary file
        model.setStartAndTargetWords("care", "card"); // Set the initial starting words and target words
        model.setRandomWordFlag(false); // 确保初始时不会随机选择单词
        GUIView view = new GUIView(model);
        view.setVisible(true);
    }
}