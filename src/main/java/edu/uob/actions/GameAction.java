package edu.uob.actions;

import edu.uob.entities.GameEntity;
import edu.uob.entities.Location;
import edu.uob.entities.NPC;
import edu.uob.entities.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameAction {
    private final Set<String> triggers = new HashSet<>();
    private final Set<String> subjects = new HashSet<>();
    private final Set<String> produces = new HashSet<>();
    private final Set<String> consumes = new HashSet<>();
    private String narration;

    public GameAction() {

    }

    // TODO: Delegate action building to factory or builder pattern
    public void addTriggers(String trigger) {
        this.triggers.add(trigger);
    }

    public void addSubjects(String subject) {
        this.subjects.add(subject);
    }

    public void addProduce(String produce) {
        this.produces.add(produce);
    }

    public void addConsume(String consume) {
        this.consumes.add(consume);
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Set<String> getTriggers() {
        return this.triggers;
    }

    public Set<String> getSubjects() {
        return this.subjects;
    }

    public Set<String> getProduces() {
        return this.produces;
    }

    public Set<String> getConsumes() {
        return this.consumes;
    }

    public String getNarration() {
        return this.narration;
    }

    public String doAction(Location storeroom, Player player, Map<String, NPC> characters) {
        doConsume(storeroom, player);
        doProduce(storeroom, player, characters);
        return getNarration();
    }

    private void doConsume(Location storeroom, Player player) {
        for(String entityName : consumes) {
            if(entityName.equals("health")) {
                player.updateHealth(1, false);
                continue;
            }
            // check player inv and move to storeroom
            GameEntity entity = player.removeFromInventory(entityName);
            if(entity == null) {
                // check current location entities and connected locations
                entity = player
                        .getCurrentLocation()
                        .removeEntities(entityName);
                entity = (entity == null) ? player
                                            .getCurrentLocation()
                                            .removePathToLocation(entityName): entity;
            }
            if(entity != null && !entity.getType().equals("location")) {
                storeroom.addEntities(entity);
            }
        }
        if(player.getHealth() <= 0) {
            player.reset();
        }
    }

    private void doProduce(Location storeroom, Player player, Map<String, NPC> characters) {
        for(String entityName : produces) {
            if(entityName.equals("health")) {
                player.updateHealth(1, true);
                continue;
            }
            // taken from storeroom
            GameEntity entity = storeroom.removeEntities(entityName);
            if(entity == null) {
                // if not, move the character to the player's location
                characters.get(entityName).goToLocation(player.getCurrentLocation());
            } else {
                storeroom.addEntities(entity);
            }
        }
    }

}
