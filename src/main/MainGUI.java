package main;

import model.Model;
import view.GUIView;

import javax.swing.*;
import java.util.Locale;

public class MainGUI {
    public static void main(String[] args) {
        // 1. Override JOptionPane button text to English
        UIManager.put("OptionPane.okButtonText",     "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        UIManager.put("OptionPane.yesButtonText",    "Yes");
        UIManager.put("OptionPane.noButtonText",     "No");

        // (Optional) Force default locale to English
        Locale.setDefault(Locale.ENGLISH);

        // 2. Now initialize model and view
        Model model = new Model();
        model.loadDictionary();                  // load words
        model.setStartAndTargetWords("care", "rest");
        model.setRandomWordFlag(false);          // start with fixed words

        // 3. Launch GUI
        SwingUtilities.invokeLater(() -> {
            GUIView view = new GUIView(model);
            view.setVisible(true);
        });
    }
}
