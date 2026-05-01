package edu.uob.entities;

public abstract class GameEntity
{
    private final String name;
    private final String description;
    private boolean consumable = false;

    public GameEntity(String name, String description, boolean consumable) {
        this(name, description);
        this.consumable = consumable;
    }

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public abstract String getType();

    public void setConsumable(boolean flag) {
        this.consumable = flag;
    }
}
