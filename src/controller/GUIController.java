package controller;

import model.Model;
import view.GUIView;

public class GUIController {
    private Model model;
    private GUIView view;

    public GUIController(Model model, GUIView view) {
        this.model = model;
        this.view = view;
    }

    public void initGame() {
        model.loadDictionary();
        model.setStartAndTargetWords("soul", "mate");
        view.setVisible(true);
    }
}