package edu.uob;

import java.util.*;

public class CommandParser {
    private CommandParser() {

    }
    public static Set<String> parse(String command) {
        // 1. lowercasing
        String commandLCase = command.toLowerCase();
        // 2. removing punctuations
        String commandWithNoPunct = commandLCase.replaceAll("[^a-z]+"," ");
        // 3. tokenization
        String[] tokens = commandWithNoPunct.split("[\\s]+");

        return new LinkedHashSet<>(Arrays.asList(tokens));
    }
}
