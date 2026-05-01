package edu.uob;

import edu.uob.actions.*;
import edu.uob.entities.Location;
import edu.uob.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultActionsTests {

    @Test
    public void testLook() {
        Location mockLoc = new Location("mockLoc", null);
        String msg = Look.look(new Player("mockPlayer", null, mockLoc));
        System.out.print(msg);
    }

    @Test
    public void testGoTo() {
        Location mockLoc = new Location("mockLoc", null);
        String msg = GoTo.goTo("mockDest", new Player("mockPlayer", null, mockLoc));
        System.out.print(msg);
    }

    @Test
    public void testInv() {
        Location mockLoc = new Location("mockLoc", null);
        String msg = Inventory.inv(new Player("mockPlayer", null, mockLoc));
        System.out.print(msg);
    }

    @Test
    public void testGet() {
        Location mockLoc = new Location("mockLoc", null);
        String msg = Get.get("mockEntity", new Player("mockPlayer", null, mockLoc));
        System.out.print(msg);
    }

    @Test
    public void testDrop() {
        Location mockLoc = new Location("mockLoc", null);
        String msg = Drop.drop("mockEntity", new Player("mockPlayer", null, mockLoc));
        System.out.print(msg);
    }

    @Test
    public void testHealth() {
        Location mockLoc = new Location("mockLoc", null);
        String msg = Health.health(new Player("mockPlayer", null, mockLoc));
        System.out.print(msg);
    }
}
