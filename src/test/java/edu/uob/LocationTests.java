package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;

import edu.uob.entities.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTests {

    @Test
    public void testAddingEntitiesToLocation() {
        // Arrange
        Location loc1 = new Location("loc1", null);
        Graph locGraph = null;
        try {
            locGraph = mockGraph();
        } catch (ParseException pe) {
            fail("ParseException happened when creating mockGraph");
        }

        // Act
        loc1.addEntities(locGraph);

        // Assert
        assertTrue(
                loc1.getEntities().get("axe").getDescription().contains("razor sharp"),
                "Entity's artefact description should be present."
        );
        assertTrue(
                loc1.getEntities().get("trapdoor").getDescription().contains("wooden trapdoor"),
                "Entity's furniture description should be present."
        );
        assertNull(loc1.getEntities().get("river"), "Location should not contain entity: river.");

    }

    public Graph mockGraph() throws ParseException{
        StringBuffer graph = new StringBuffer("digraph layout {\n" +
                "    splines=ortho;\n" +
                "    node [shape=\"rect\"];\n" +
                "    subgraph cluster001 {\n" +
                "        node [shape=\"none\"];\n" +
                "        cabin [description=\"A log cabin in the woods\"];\n" +
                "        subgraph artefacts {\n" +
                "            node [shape=\"diamond\"];\n" +
                "            potion [description=\"A bottle of magic potion\"];\n" +
                "            axe [description=\"A razor sharp axe\"];\n" +
                "            coin [description=\"A silver coin\"];\n" +
                "        }\n" +
                "        subgraph furniture {\n" +
                "            node [shape=\"hexagon\"];\n" +
                "            trapdoor [description=\"A locked wooden trapdoor in the floor\"];\n" +
                "        }\n" +
                "        subgraph obstacles {\n" +
                "            node [shape=\"none\"];\n" +
                "            river\n" +
                "        }\n" +
                "    }\n" +
                "}");

        Parser p = new Parser();
        p.parse(graph);
        return p.getGraphs().get(0).getSubgraphs().get(0);
    }

}
