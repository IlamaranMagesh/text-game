package edu.uob.actions;

import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.entities.Player;

public class Drop {
    private Drop() {

    }

    public static String drop(String entityName, Player player) {
        String narration;
        GameEntity entity = player.removeFromInventory(entityName);
        Location currentLocation = player.getCurrentLocation();

        if(entity == null) {
            narration = "No " + entityName + " in your inventory.";
        } else {
            narration = entityName + " item dropped.";
            currentLocation.addEntities(entity);
        }

        return narration + "\n";
    }
}
