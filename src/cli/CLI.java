package cli;

import model.Model;

import java.util.List;
import java.util.Scanner;

/**
 * Command-Line Interface (CLI) for the Weaver game.
 *
 * Features:
 *  - “new” / “newgame” to start a fresh game
 *  - “reset” to undo the last guess
 *  - “error on/off” to toggle invalid-word prompts
 *  - “path on/off” to toggle showing the [start → target] path
 *  - “quit” / “exit” to end the program
 *  - Validates guesses against the dictionary
 *  - Displays start/target on new game and full history after each guess
 */
public class CLI {
    private final Model model;
    private final Scanner scanner;

    public CLI(Model model) {
        this.model   = model;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main loop: reads commands and guesses, updates the Model, and displays feedback.
     */
    public void start() {
        System.out.println("Welcome to Weaver (CLI)!");
        printHelp();

        boolean running = true;
        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd  = line.toLowerCase();

            switch (cmd) {
                case "help":
                    printHelp();
                    break;

                case "new":
                case "newgame":
                    startNewGame();
                    break;

                case "reset":
                    model.removeLastInput();
                    System.out.println("Last guess removed.");
                    printHistory();
                    break;

                case "error on":
                    model.setShowErrorFlag(true);
                    System.out.println("Invalid-word prompts: ON");
                    break;
                case "error off":
                    model.setShowErrorFlag(false);
                    System.out.println("Invalid-word prompts: OFF");
                    break;

                case "path on":
                    model.setShowPathFlag(true);
                    System.out.println("Path display: ON");
                    break;
                case "path off":
                    model.setShowPathFlag(false);
                    System.out.println("Path display: OFF");
                    break;

                case "quit":
                case "exit":
                    running = false;
                    break;

                default:
                    // Treat anything else as a guess
                    handleGuess(line);
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    /** Print available commands. */
    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  new/newgame    Start a new game");
        System.out.println("  reset          Remove last guess");
        System.out.println("  error on/off   Toggle invalid-word prompts");
        System.out.println("  path on/off    Toggle showing [start → target] path");
        System.out.println("  help           Show this help message");
        System.out.println("  quit/exit      Exit the program");
        System.out.println("Or just type a 4-letter word to guess.");
    }

    /** Initialize or reset start/target and clear history. */
    private void startNewGame() {
        model.loadDictionary();
        model.setRandomWordFlag(true);
        model.setStartAndTargetWords(null, null);
        model.resetGame();
        System.out.println("New game started!");
        System.out.println("  Start:  " + model.getStartWord());
        System.out.println("  Target: " + model.getTargetWord());
    }
    /**
     * Handle a word guess:
     *  - Validate length
     *  - Check dictionary membership
     *  - Update model and display history
     *  - Show path or victory if applicable
     */
    private void handleGuess(String guess) {
        // Ensure a game is started
        String start = model.getStartWord();
        if (start == null) {
            System.out.println("Please start a game first with 'new'.");
            return;
        }

        // Enforce correct length
        if (guess.length() != start.length()) {
            System.out.println("Please enter a " + start.length() + "-letter word.");
            return;
        }


        // Check dictionary membership
        if (!model.isValidWord(guess)) {
            // If error messaging enabled, print the model's error message
            String err = model.getErrorMessage();
            System.out.println(err != null ? err : "Word not in dictionary!");
            return;
        }

        // Valid guess: update model
        model.updateCurrentInput(guess);

        // Optionally print path
        List<String> path = model.getPath();
        if (path != null) {
            System.out.println("Path: " + path);
        }

        // Print history after update
        printHistory();

        // Check for win
        if (model.checkWin()) {
            System.out.println("Congratulations! You reached the target!");
        }
    }

    /** Display the full guess history. */
    private void printHistory() {
        List<String> history = model.getHistoryWords();
        System.out.println("History: " + history);
    }

    public static void main(String[] args) {
        Model model = new Model();
        CLI cli   = new CLI(model);
        cli.start();
    }
}
