package edu.uob.entities;

public class GameFurniture extends GameEntity {

    public GameFurniture(String name, String description) {
        super(name, description);
    }

    public String getType() {
        return "furniture";
    }
}
