package edu.uob.entities.collection;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.entities.Location;

import java.util.*;
import java.util.logging.Logger;

public class LocationCollection implements Iterable<Location> {
    private static final Logger logger = Logger.getLogger(LocationCollection.class.getName());
    private static final Map<String, Location> locationsRegistry = new HashMap<>();

    /**
     * A Collection that holds all the locations in the game. Generated from the entity config.
     *
     */
    public LocationCollection() {
    }

    public Location getLocation(String locationName) {
        return locationsRegistry.get(locationName);
    }

    public void addLocation(Location location) {
        locationsRegistry.put(location.getName(), location);
    }

    public Set<String> getLocationNames() {
        return locationsRegistry.keySet();
    }

    @Override
    public Iterator<Location> iterator() {
        return null;
    }
}
