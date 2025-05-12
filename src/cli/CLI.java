package cli;

import model.Model;

import java.util.List;
import java.util.Scanner;
/**
 * CLI provides a console-based interface for playing the Weaver word game.
 * It supports commands for guessing words, resetting input, and starting new rounds.
 */
public class CLI {
    private Model model;
    private boolean showErrorsFlag = true;
    private boolean showSolutionPathFlag = true;
    private boolean randomGameFlag = true;
    /**
     * Constructor injects a shared Model instance and initializes display flags.
     */
    public CLI(Model model) {
        this.model   = model;
        model.setShowErrorFlag(showErrorsFlag);
        model.setShowPathFlag(showSolutionPathFlag);
        model.setRandomWordFlag(randomGameFlag);
    }
    /**
     * Starts the main game loop: loads dictionary, then repeats rounds until exit.
     */
    public void start() {
        System.out.println("Welcome to Weaver (CLI)!");
        model.loadDictionary();


        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // Clear history at the start of each round
            model.resetGame();
            model.setRandomWordFlag(randomGameFlag);
            model.setStartAndTargetWords(null, null);  // pick new random (or fixed) words

            System.out.println("Game started. Start Word: " + model.getStartWord() + ", Target Word: " + model.getTargetWord());
            System.out.println("Flags: [Show Errors: " + showErrorsFlag + "] [Show Solution Path: " + showSolutionPathFlag + "] [Random Game: " + randomGameFlag + "]");
            // Show solution path at the start
            if (showSolutionPathFlag) {
                List<String> path = model.getSolutionPath();
                if (path != null) {
                    System.out.println("Solution Path: " + String.join(" → ", path));
                } else {
                    System.out.println("No valid path found!");
                }
            }

            boolean roundOver = false;
            while (!roundOver) {
                System.out.print("Enter a word or 'reset' to reset the last input: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("reset")) {
                    model.resetGame();
                    System.out.println("All input cleared.");
                    if (showSolutionPathFlag) {
                        List<String> path = model.getSolutionPath();
                        if (path != null) {
                            System.out.println("Solution Path: " + String.join(" → ", path));
                        } else {
                            System.out.println("No valid path found!");
                        }
                    }
                    continue;
                }

                if (!model.isValidWord(input)) {
                    if (showErrorsFlag) {
                        System.out.println("Invalid word!");
                    }
                    if (showSolutionPathFlag) {
                        List<String> path = model.getSolutionPath();
                        if (path != null) {
                            System.out.println("Solution Path: " + String.join(" → ", path));
                        } else {
                            System.out.println("No valid path found!");
                        }
                    }
                    continue;
                }

                String lastWord = model.getHistoryWords().isEmpty() ? model.getStartWord() : model.getHistoryWords().get(model.getHistoryWords().size() - 1);
                int diff = model.countLetterDifferences(input, lastWord);
                if (diff > 1) {
                    if (showErrorsFlag) {
                        System.out.println("You can only change one letter at a time!");
                    }
                    if (showSolutionPathFlag) {
                        List<String> path = model.getSolutionPath();
                        if (path != null) {
                            System.out.println("Solution Path: " + String.join(" → ", path));
                        } else {
                            System.out.println("No valid path found!");
                        }
                    }
                    continue;
                }

                model.updateCurrentInput(input);

                List<String> historyWords = model.getHistoryWords();
                System.out.println("History: " + historyWords);

                if (showSolutionPathFlag) {
                    List<String> path = model.getSolutionPath();
                    if (path != null) {
                        System.out.println("Solution Path: " + String.join(" → ", path));
                    } else {
                        System.out.println("No valid path found!");
                    }
                }

                if (input.equalsIgnoreCase(model.getTargetWord())) {
                    System.out.println("You win!");
                    // Ask whether a new round of the game will be carried out
                    System.out.print("Start a new game? (y/n): ");
                    String yn = scanner.nextLine().trim().toLowerCase();
                    if (yn.equals("y")) {
                        roundOver = true; // Exit the current round and move on to the next round
                    } else {
                        running = false; // End the outer loop and exit the program
                        roundOver = true;
                    }
                }
            }
        }
        scanner.close();
    }



    public static void main(String[] args) {
        Model model = new Model();
        CLI cli   = new CLI(model);
        cli.start();
    }
}
