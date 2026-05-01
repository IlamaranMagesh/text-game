package edu.uob;

import edu.uob.actions.ActionRegistrar;
import edu.uob.actions.DefaultActions;
import edu.uob.entities.EntityRegistrar;

import java.util.*;

import static edu.uob.Config.NGRAM;

public class Tagger {

    private Tagger() {
    }

    public static Map<String, Set<String>> tag(Set<String> tokens, EntityRegistrar entityRegistrar,
                                        ActionRegistrar actionRegistrar) {
        Map<String, Set<String>> taggedTokens = new HashMap<>();

        // Adding default actions
        for(DefaultActions defaultAction : DefaultActions.values()) {
            String defaultActionName = defaultAction.name().toLowerCase();
            if(tokens.remove(defaultActionName)) {
                taggedTokens.computeIfAbsent("actions", (k) -> new HashSet<>()).add(defaultActionName);
            }
        }

        // Adding action phrases
        String tokenString = String.join(" ", tokens);
        for(String actionPhrase : actionRegistrar.getActionPhrases()) {
            if(tokenString.contains(actionPhrase)) {
                taggedTokens.computeIfAbsent("actions", (k) -> new HashSet<>()).add(actionPhrase);
                tokenString = tokenString.replaceFirst(actionPhrase, "");
            }
        }

        // Checking the remaining tokens for subjects
        for(String token : tokenString.split(" ")) {
           if(entityRegistrar.containsEntity(token)) {
                taggedTokens.computeIfAbsent("subjects", (k) -> new HashSet<>()).add(token);
            }
        }

        return taggedTokens;
    }

}
