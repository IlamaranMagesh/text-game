package edu.uob.actions;

import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.entities.Player;

import java.util.Collection;

public class Look {
    private Look() {}

    public static String look(Player player) {
        return lookIntoLocation(player)
                + lookIntoPaths(player.getCurrentLocation());
    }

    // TODO multiple duplicate snippets. Can be made DRY
    private static String lookIntoPaths(Location location) {
        StringBuilder narration = new StringBuilder();
        Collection<Location> paths = location.getConnectedLocations().values();
        // TODO Add ChoiceFormat if gets complicated
        switch (paths.size()) {
            case 0 -> narration.append("You see no paths. You are stuck here and need to find a way out!\n");
            case 1 -> narration.append("You see a path leading to:\n");
            default -> narration.append("You see paths leading to:\n");
        }
        if(!paths.isEmpty()) {
            for(Location nextLocation : paths) {
                String desc = nextLocation.getDescription();
                narration.append(nextLocation.getName());
                if(desc != null && !desc.isBlank()) {
                    narration.append(": ")
                            .append(desc);
                }
                narration.append("\n");
            }
        }
        return narration.toString();
    }

    private static String lookIntoLocation(Player player) {
        StringBuilder narration = new StringBuilder();
        Location location = player.getCurrentLocation();
        narration.append("You look around and you find yourself in ");
        // location description
        String locationDesc = location.getDescription();
        String locationName = location.getName();
        narration.append(locationName);
        if(locationDesc != null && !locationDesc.isBlank()) {
            narration.append(": ")
                    .append(locationDesc.toLowerCase());
        } else {
            narration.append(".");
        }
        narration.append("\n");

        // list out entities
        Collection <GameEntity> entities = player.lookCurrentLocation().values();
        if (entities.isEmpty()) {
            narration.append("You see nothing in here. It is an empty void.\n");
        } else {
            narration.append("You see:\n");
        }
        for(GameEntity entity : entities){
            String name = entity.getName();
            String desc = entity.getDescription();

            // skipped if the entity is the player themselves
            if(entity.getType().equals("players") && player.getName().equals(name)) {
                continue;
            }

            narration.append(name);
            if(desc != null && !desc.isBlank()) {
                narration.append(": ")
                        .append(desc);
            }
            narration.append("\n");
        }

        return narration.toString();
    }

}
