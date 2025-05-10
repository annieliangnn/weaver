package cli;

import model.Model;
import model.Model.GameState;

import java.util.List;
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
        boolean running = true;

        System.out.println("Game started. Start Word: " + model.getStartWord() + ", Target Word: " + model.getTargetWord());

        while (running) {
            System.out.print("Enter a word or 'reset' to reset the last input: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("reset")) {
                model.removeLastInput();
                System.out.println("Last input cleared.");
                continue;
            }

            model.updateCurrentInput(input);

            if (model.getErrorMessage() != null) {
                System.out.println(model.getErrorMessage());
            }

            List<String> historyWords = model.getHistoryWords();
            System.out.println("History: " + historyWords);

            // Directly compare the user input and the target word in the CLI class
            if (input.equalsIgnoreCase(model.getTargetWord())) {
                System.out.println("Congratulations!");
                running = false;
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        Model model = new Model();
        CLI cli = new CLI(model);
        cli.start();
    }
}