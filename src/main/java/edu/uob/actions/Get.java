package edu.uob.actions;

import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.entities.Player;

public final class Get {
    private Get() {

    }
    public static String get(String entityName, Player player){
        String narration;
        Location currentLocation = player.getCurrentLocation();
        GameEntity entity = currentLocation.getEntities().get(entityName);

        if(entity == null) {
            narration = "No " + entityName + " here.";
        } else if(entity.getType().equals("artefacts")) {
            narration = "You picked up " + entityName + ".";
            player.addToInventory(entityName);
        } else {
            narration = entityName + " cannot be picked up.";
        }
        return narration + "\n";
    }
}
