package edu.uob.entities;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Location extends GameEntity {
    private static final Logger logger = Logger.getLogger(Location.class.getName());

    private final Map<String, GameEntity> entities = new HashMap<>();
    private final Map<String, Location> connectedLocations = new HashMap<>();

    /**
     * A data structure for {@code location}.
     *
     * @param name A name or id of the location
     * @param description A short note about the location
     */
    public Location(String name, String description) {
        super(name, description);
    }

    @Override
    public String getType() {
        return "location";
    }

    /**
     * Adds entities present in the location to that location entity object
     *
     * @param graphs A location graph that holds artefacts and furniture
     */
    public void addEntities(Graph graphs) {
        logger.info("Adding entities to the location: " + this.getName());
        for(Graph subGraph : graphs.getSubgraphs()) {
            String entityType = subGraph.getId().getId();
            logger.info("Adding entity type: " + entityType + " to registry.");
            List<Node> newEntities = subGraph.getNodes(true);
            addEntitiesToRegistry(newEntities, entityType);
        }
    }

    /**
     *  Adds entity to the location
     *
     * @param entity A GameEntity that is anything except Location entity
     */
    public void addEntities(GameEntity entity) {
        entities.put(entity.getName(), entity);
    }

    /**
     * Creates {@code GameEntity} objects and adds them to the location registry
     *
     * @param newEntities List of entity nodes
     * @param entityType Type of entity. {@code artefacts} or {@code furniture}
     */
    private void addEntitiesToRegistry(List<Node> newEntities, String entityType) {
        for(Node entityNode : newEntities) {
            String name = entityNode.getId().getId();
            String desc = entityNode.getAttribute("description");
            GameEntity entity;
            switch (entityType) {
                case "artefacts" -> entity = new GameArtefact(name, desc);
                case "furniture" -> entity = new GameFurniture(name, desc);
                case "characters" -> entity = new NPC(name, desc, this);
                default -> {
                    String msg = String.format("Entity Type: %s is not present in the game and not added to registry",
                            entityType);
                    logger.warning(msg);
                    continue;
                }
            }
            this.entities.put(name, entity);
            logger.info("Added " + entityType + ": " + name);
        }
    }

    /**
     * Adds a path to the location
     *
     * @param destination A location object to which a path needs to be created
     */
    public void connectToLocation(Location destination) {
        connectedLocations.put(destination.getName(), destination);
    }

    /**
     * Returns all currently present entities in the location
     *
     * @return A Map that holds all the entities in the location with their name as keys
     */
    public Map<String, GameEntity> getEntities() {
        return this.entities;
    }

    /**
     * Returns all currently connected locations
     *
     * @return A Map that holds all the locations with their name as keys
     */
    public Map<String, Location> getConnectedLocations() {
        return this.connectedLocations;
    }

    public GameEntity removeEntities(String name) {
        return entities.remove(name);
    }

    public Location removePathToLocation(String name) {
        return connectedLocations.remove(name);
    }
}
