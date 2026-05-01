package edu.uob.entities;

import java.util.HashMap;
import java.util.Map;

public class Player extends NPC {
    private final Location startingLocation;
    public Player(String name, String description, Location currentLocation) {
        super(name, description, currentLocation);
        startingLocation = currentLocation;
    }

    // get action
    public void addToInventory(String entityName) {
        GameEntity entity = this.getCurrentLocation().removeEntities(entityName);
        if(entity != null) {
            this.inventory.put(entityName, entity);
        }
    }

    // drop action
    public GameEntity removeFromInventory(String entityName) {
        return this.inventory.remove(entityName);
    }

    // inv or inventory action
    public Map<String, GameEntity> getInventory() {
        return this.inventory;
    }

    // look action
    public Map<String, GameEntity> lookCurrentLocation() {
        return currentLocation.getEntities();
    }

    public void reset() {
        // Make full health
        this.updateHealth(3, true);

        // Add entities to current Location
        for(Map.Entry<String, GameEntity> inv : this.getInventory().entrySet()) {
            this.currentLocation.addEntities(inv.getValue());
        }

        // clear player's inventory
        this.inventory.clear();

        // move to starting location
        this.goToLocation(this.startingLocation);
    }

    public Location getStartingLocation() {
        return this.startingLocation;
    }

    @Override
    public String getType() {
        return "players";
    }
}
