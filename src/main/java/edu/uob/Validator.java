package edu.uob;

import edu.uob.actions.ActionRegistrar;
import edu.uob.actions.DefaultActions;
import edu.uob.actions.GameAction;
import edu.uob.entities.EntityRegistrar;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Validator {
    private Validator() {

    }

    public static GameAction validate(Map<String, Set<String>> taggedTokens, ActionRegistrar actionRegistrar) {
        Set<String> actionPhrases = taggedTokens.get("actions");
        Set<String> subjects = taggedTokens.get("subjects");
        if(actionPhrases == null || actionPhrases.isEmpty()) {
            return null;
        }

        GameAction gameAction = null;

        for(String actionPhrase : actionPhrases) {
            List<GameAction> actions = actionRegistrar.getAction(actionPhrase);
            for(GameAction action : actions) {
                if(action.getSubjects().containsAll(subjects)) {
                    if(gameAction == null) {
                        gameAction = action;
                    }
                    if(!gameAction.equals(action)){
                        return null;
                    }
                }
            }
        }

        return gameAction;
    }

    public static boolean validateDefaultAction(Map<String, Set<String>> taggedTokens, ActionRegistrar actionRegistrar) {
        Set<String> actionPhrases = taggedTokens.get("actions");
        Set<String> subjects = taggedTokens.get("subjects");

        if(actionPhrases == null || actionPhrases.size() != 1) {
            return false;
        }

        String actionPhrase = actionPhrases.iterator().next();
        DefaultActions action;
        try {
            action = DefaultActions.valueOf(actionPhrase.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        int subjectCount = (subjects == null) ? 0 : subjects.size();

        return switch (action) {
            case LOOK, HEALTH, INVENTORY, INV -> subjectCount == 0;
            case GET, DROP, GOTO -> subjectCount == 1;
        };
    }
}
