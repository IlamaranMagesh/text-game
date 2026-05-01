package edu.uob.actions;

import edu.uob.entities.GameEntity;
import edu.uob.entities.Player;

import java.util.Collection;

public class Inventory {
    private Inventory() {

    }

    public static String inv(Player player) {
        StringBuilder narration = new StringBuilder();
        narration.append("You have");
        Collection<GameEntity> items = player.getInventory().values();

        if(items.isEmpty()) {
            narration.append(" no items.\n");
        } else {
            narration.append(": \n");
            for(GameEntity item : items) {
                String desc = item.getDescription();
                narration.append(item.getName());
                if(desc != null && !desc.isBlank()) {
                    narration.append(": ")
                            .append(desc);
                }
                narration.append("\n");
            }
        }
        return narration.toString();
    }
}
