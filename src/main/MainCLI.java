package main;

import cli.CLI;
import model.Model;

public class MainCLI {
    public static void main(String[] args) {
        Model model = new Model();
        CLI cli = new CLI(model);
        cli.start();
    }
}