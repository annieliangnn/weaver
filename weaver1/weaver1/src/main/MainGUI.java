package main;

import model.Model;
import view.GUIView;

public class MainGUI {
    public static void main(String[] args) {
        Model model = new Model();
        model.loadDictionary(); // 加载字典文件
        model.setStartAndTargetWords("soul", "mate"); // 设置初始的起始词和目标词
        GUIView view = new GUIView(model);
        view.setVisible(true);
    }
}