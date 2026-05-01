package edu.uob.entities;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.entities.collection.LocationCollection;

import java.util.*;
import java.util.logging.Logger;

public final class EntityRegistrar {
    private static final Logger logger = Logger.getLogger(EntityRegistrar.class.getName());
    private final LocationCollection locations;
    // TODO better way of storing entities as others are registered with their objects
    private final Map<String, String> entities;
    private final Map<String, Player> players;
    private final Map<String, NPC> NPCs;
    private String startingLocation;

    public EntityRegistrar() {
        // Instantiate collections
        this.locations = new LocationCollection();
        this.entities = new HashMap<>();
        this.players = new HashMap<>();
        this.NPCs = new HashMap<>();
    }

    public void createEntities(List<Graph> graphs) {
        // Handle config file structure
        if(graphs.isEmpty()) {
            throw new IndexOutOfBoundsException("Entity file should contain at least one graph!");
        }

        Graph locationGraph = graphs.get(0).getSubgraphs().get(0);
        Graph paths = null;

        if(graphs.get(0).getSubgraphs().size() == 2) {
            paths = graphs.get(0).getSubgraphs().get(1);
        }

        buildLocations(locationGraph);
        connectLocations(paths);
    }

    /**
     * A method that builds location entities along with its artefacts and furniture.
     * Each location is added to the collection.
     *
     * @param locationGraph A graph that holds at least one location.
     * @throws IndexOutOfBoundsException Throws the error when there is no location or a name for the location.
     */
    private void buildLocations(Graph locationGraph) {
        logger.info("Building locations");
        if(locationGraph.getSubgraphs().isEmpty()) {
            throw new IndexOutOfBoundsException("There must be at least one location in the entity config.");
        }

        for(Graph subGraph : locationGraph.getSubgraphs()) {
            Node locationNode;
            try {
                locationNode = subGraph.getNodes(true).get(0);
            } catch (IndexOutOfBoundsException e) {
                String locID = subGraph.getId().getId();
                String msg = String.format("Location: %s needs to have at least one node for its name.", locID);
                throw new IndexOutOfBoundsException(msg);
            }

            String name = locationNode.getId().getId();
            String desc = locationNode.getAttribute("description");
            Location location = new Location(name, desc);

            // create entities present in the location and add them to its own registry
            location.addEntities(subGraph);

            Map<String, GameEntity> newEntities = location.getEntities();

            // register entities and location to the global registry
            this.registerEntities(newEntities);
            locations.addLocation(location);

            if(this.startingLocation == null) {
                this.startingLocation = name;
            }

            logger.info("Location: " + name + " added to the location registry");
        }

        if(locations.getLocation("storeroom") == null) {
            logger.info("storeroom is not present. Creating default storeroom.");
            Location storeRoom = new Location("storeroom",
                    "Storage for any entities not placed in the game");
            locations.addLocation(storeRoom);
        }

    }

    public void registerEntities(Map<String, GameEntity> newEntities) {
        for(Map.Entry<String, GameEntity> entity : newEntities.entrySet()) {
            if(entity.getValue().getType().equals("characters")) {
                NPC npc = (NPC)entity.getValue();
                NPCs.put(entity.getKey(), npc);
            }
            entities.put(entity.getKey(), entity.getValue().getType());
        }
    }

    /**
     * Adds edges to the location nodes. If {@code paths} is empty, edges are not created.
     *
     * @param paths A graph that contains edges to locations
     */
    private void connectLocations(Graph paths) {
        if(paths == null) {
            return;
        }
        Set<String> reachedLocations = new HashSet<>();
        for(Edge path : paths.getEdges()) {
            String source = path
                    .getSource()
                    .getNode()
                    .getId()
                    .getId();

            String dest = path
                    .getTarget()
                    .getNode()
                    .getId()
                    .getId();

            Location sourceLocation = locations.getLocation(source);
            Location destLocation = locations.getLocation(dest);

            try {
                if(sourceLocation.getName().equals("storeroom") || destLocation.getName().equals("storeroom")) {
                    throw new IllegalArgumentException();
                }
            } catch (NullPointerException npe) {
                String msg = String.format("%s: %s -> %s path not built as one or both of " +
                        "the locations are not present in the entity config", npe, source, dest);
                logger.severe(msg);
                continue;
            } catch (IllegalArgumentException iae) {
                String msg = String.format("%s: %s -> %s path not built as storeroom" +
                        "cannot be connected to any location", iae, source, dest);
                logger.severe(msg);
                continue;
            }

            reachedLocations.add(destLocation.getName());
            sourceLocation.connectToLocation(destLocation);
        }

        // add not reached locations to storeroom
        addUnreachableLocToStoreRoom(reachedLocations);
    }

    private void addUnreachableLocToStoreRoom(Set<String> reachedLocations) {
        Location storeRoom = locations.getLocation("storeroom");
        for(String location : locations.getLocationNames()) {
            if(!reachedLocations.contains(location)) {
                storeRoom.addEntities(locations.getLocation(location));
            }
        }
    }

    public void moveToStoreRoom(GameEntity entity) {
        locations.getLocation("storeroom").addEntities(entity);
    }

    public Map<String, String> getEntitiesMap() {
        return entities;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Map<String, NPC> getNPCs() {
        return NPCs;
    }

    public LocationCollection getLocations() {
        return locations;
    }

    public boolean containsEntity(String entityName) {
        return players.containsKey(entityName) ||
                locations.getLocationNames().contains(entityName) ||
                NPCs.containsKey(entityName) ||
                entities.containsKey(entityName);
    }

    public void registerPlayer(String name) {
        String desc = "A wild sentient being";
        Location currentLocation = locations.getLocation(startingLocation);
        players.computeIfAbsent(name, (k) -> new Player(name, desc, currentLocation));

        // add player to the location entities
        currentLocation.addEntities(players.get(name));
    }

    public void setStartingLocation(String startingLocations) {
        this.startingLocation = startingLocations;
    }

}
