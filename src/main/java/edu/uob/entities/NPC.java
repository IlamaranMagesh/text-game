package edu.uob.entities;

import java.util.HashMap;
import java.util.Map;

import static edu.uob.Config.*;

public class NPC extends GameEntity{
    Map<String, GameEntity> inventory;
    int health;
    Location currentLocation;

    public NPC(String name, String description, Location currentLocation) {
        super(name, description);
        this.currentLocation = currentLocation;
        this.inventory = new HashMap<>();
        this.health = HEALTH;
    }

    // potion or poison
    public int updateHealth(int value, boolean heal) {
        if (heal) {
            this.health = Math.max(this.health + value, HEALTH);
        } else {
            this.health = Math.min(this.health - value, 0);
        }
        return this.health;
    }

    // when character is produced, it moves from its current location to player's location
    public void goToLocation(Location location) {
        // remove itself from current location
        this.currentLocation.removeEntities(this.getName());
        // change its current location and add itself to the player's location
        this.currentLocation = location;
        this.currentLocation.addEntities(this);
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    public int getHealth() {
        return this.health;
    }

    @Override
    public String getType() {
        return "characters";
    }
}
