package cli;

import model.Model;

import java.util.List;
import java.util.Scanner;

public class CLI {
    private Model model;
    private boolean showErrorsFlag = true;
    private boolean showSolutionPathFlag = true;
    private boolean randomGameFlag = true;
    public CLI(Model model) {
        this.model   = model;
        model.setShowErrorFlag(showErrorsFlag);
        model.setShowPathFlag(showSolutionPathFlag);
        model.setRandomWordFlag(randomGameFlag);
    }

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
                    // 询问是否进行新一轮游戏
                    System.out.print("Start a new game? (y/n): ");
                    String yn = scanner.nextLine().trim().toLowerCase();
                    if (yn.equals("y")) {
                        roundOver = true; // 跳出当前回合，进入下一轮
                    } else {
                        running = false; // 结束外层循环，退出程序
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
