package edu.uob.actions;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public final class ActionRegistrar {
    // TODO: make it to List<GameAction>
    private final Map<String, List<GameAction>> actionsRegistry;

    public ActionRegistrar() {
        this.actionsRegistry = new HashMap<>();
    }

    public List<GameAction> getAction(String name) {
        return this.actionsRegistry.get(name);
    }

    public Set<String> getActionPhrases() {
        return this.actionsRegistry.keySet();
    }

    public void createActions(NodeList actions) {
        for(int i = 0; i < actions.getLength(); i++) {
            Node action = actions.item(i);

            // TODO: make phraseTypes to enum
            // trigger phrases
            List<String> triggerPhrases = this.getContent(action, "triggers", "keyphrase");
            // subject phrases
            List<String> subjectPhrases = this.getContent(action, "subjects", "entity");
            // produce phrases
            List<String> producePhrases = this.getContent(action, "produced", "entity");
            // consume phrases
            List<String> consumePhrases = this.getContent(action, "consumed", "entity");
            // narration phrase
            List<String> narration = this.getContent(action, "narration", null);

            if (narration.isEmpty() || triggerPhrases.isEmpty()) {
                continue;
            }
            GameAction gameAction = new GameAction();
            this.addPhrases(gameAction, triggerPhrases, "triggers");
            this.addPhrases(gameAction, subjectPhrases, "subjects");
            this.addPhrases(gameAction, producePhrases, "produced");
            this.addPhrases(gameAction, consumePhrases, "consumed");
            gameAction.setNarration(narration.get(0));

            // register action with its trigger phrases
            addToRegistry(gameAction, triggerPhrases);
        }
    }

    private void addToRegistry(GameAction action, Iterable<String> triggerPhrases) {
        for(String trigger : triggerPhrases) {
            actionsRegistry.computeIfAbsent(trigger, (k) -> new ArrayList<>()).add(action);
        }
    }

    private void addPhrases(GameAction action, Iterable<String> phrases, String phraseType) {
        for(String phrase : phrases) {
            switch (phraseType) {
                case "triggers" -> action.addTriggers(phrase);
                case "subjects" -> action.addSubjects(phrase);
                case "produced" -> action.addProduce(phrase);
                case "consumed" -> action.addConsume(phrase);
                default -> throw new IllegalArgumentException("Invalid phraseType");
            }
        }
    }

    private List<String> getContent(Node action, String parentTag, String childTag) {
        List<String> content = new ArrayList<>();
        if(action.getNodeType() == Node.ELEMENT_NODE) {
            Element actionElement = (Element) action;

            if(parentTag.equals("narration")) {
                NodeList nodes = actionElement.getElementsByTagName(parentTag);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getParentNode().equals(actionElement)) {
                        content.add(node.getTextContent());
                    }
                }
                return content;
            }

            NodeList parents = actionElement.getElementsByTagName(parentTag);
            for (int j = 0; j < parents.getLength(); j++) {
                Node parent = parents.item(j);
                if (parent.getNodeType() == Node.ELEMENT_NODE && parent.getParentNode().equals(actionElement)) {
                    Element parentElement = (Element) parent;
                    NodeList children = parentElement.getElementsByTagName(childTag);
                    for (int k = 0; k < children.getLength(); k++) {
                        content.add(children.item(k).getTextContent());
                    }
                }
            }
        }
        return content;
    }
}
