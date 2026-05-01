package edu.uob.actions;

import edu.uob.entities.Player;

public class Health {
    private Health() {

    }

    public static String health(Player player) {
        return "You have " + player.getHealth() + " health.\n";
    }
}
