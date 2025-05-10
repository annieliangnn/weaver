package cli;

import model.Model;
import model.Model.GameState;

import java.util.Scanner;

public class CLI {
    private Model model;

    public CLI(Model model) {
        this.model = model;
    }

    public void start() {
        model.loadDictionary();
        model.setStartAndTargetWords("word", "goal");

        Scanner scanner = new Scanner(System.in);
        while (model.getGameState() == GameState.IN_PROGRESS) {
            System.out.print("Enter a word: ");
            String input = scanner.nextLine();
            model.updateCurrentInput(input);
            if (model.getErrorMessage() != null) {
                System.out.println(model.getErrorMessage());
            }
            if (model.getPath() != null) {
                System.out.print("Path: ");
                for (String word : model.getPath()) {
                    System.out.print(word + " ");
                }
                System.out.println();
            }
            if (model.checkWin()) {
                System.out.println("You win!");
            }
        }
        scanner.close();
    }
}