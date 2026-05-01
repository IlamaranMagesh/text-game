package edu.uob.actions;

import edu.uob.entities.Location;
import edu.uob.entities.Player;

public class GoTo {

    private GoTo() {

    }
    public static String goTo(String destLocation, Player player) {
        Location currentLocation = player.getCurrentLocation();
        Location destination = currentLocation.getConnectedLocations().get(destLocation);
        String narration;

        // check if the location is connected
        if(destination != null) {
            narration = "You moved to " + destLocation;
            player.goToLocation(destination);
        } else {
            narration = "There is no path to " + destLocation + ". You stand still.";
        }
        return narration + "\n";
    }
}
