package main;

import cli.CLI;
import model.Model;

import java.util.List;

public class MainCLI {
    private static List<String> list = null;
    public static void main(String[] args) {
        Model model = new Model();
        CLI cli = new CLI(model);
        cli.start();
    }
}