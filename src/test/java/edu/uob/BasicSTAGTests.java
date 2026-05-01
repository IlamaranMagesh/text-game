package edu.uob;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class BasicSTAGTests {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);

    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testBasicMovement() {
        String response = sendCommandToServer("steve: goto forest");
        assertTrue(response.toLowerCase().contains("forest"), "Should be in forest now");
        response = sendCommandToServer("steve: look");
        assertTrue(response.toLowerCase().contains("key"), "Should see key in forest");

        response = sendCommandToServer("steve: goto cabin");
        assertTrue(response.toLowerCase().contains("cabin"), "Should be back in cabin");
    }

    @Test
    void testInventoryAndDrop() {
        sendCommandToServer("steve: get axe");
        String response = sendCommandToServer("steve: inv");
        assertTrue(response.toLowerCase().contains("axe"), "Axe should be in inventory");

        sendCommandToServer("steve: drop axe");
        response = sendCommandToServer("steve: inv");
        assertFalse(response.toLowerCase().contains("axe"), "Axe should not be in inventory after drop");

        response = sendCommandToServer("steve: look");
        assertTrue(response.toLowerCase().contains("axe"), "Axe should be in the room after drop");
    }

    @Test
    void testInvalidCommands() {
        String response = sendCommandToServer("steve: fly to moon");
        assertTrue(response.toLowerCase().contains("no valid action"), "Server should return a message for invalid commands");
    }


    @Test
    void testCaseInsensitivity() {
        String response = sendCommandToServer("steve: GET AXE");
        assertTrue(response.toLowerCase().contains("axe"), "Should be case insensitive");

        response = sendCommandToServer("steve: INV");
        assertTrue(response.toLowerCase().contains("axe"), "Should be case insensitive");

        response = sendCommandToServer("steve: GOTO FOREST");
        assertTrue(response.toLowerCase().contains("forest"), "Movement should be case insensitive");
    }

    @Test
    void testExtraneousWords() {
        // "please can I look around the room" should trigger "look"
        String response = sendCommandToServer("steve: please can I look around the room");
        assertTrue(response.toLowerCase().contains("cabin"), "Should still trigger look");

        // "pick up the axe" should not trigger "get axe"
        sendCommandToServer("steve: pick up the axe");
        response = sendCommandToServer("steve: inv");
        assertFalse(response.toLowerCase().contains("axe"), "Should not trigger get");
    }

    @Test
    void testAmbiguousCommands() {
        // Some default actions need subjects. "get", "goto", "drop" require one subject
        String response = sendCommandToServer("steve: get");
        assertTrue(response.toLowerCase().contains("no valid action"), "Default actions should not do ambiguous actions");

        response = sendCommandToServer("steve: goto");
        assertTrue(response.toLowerCase().contains("no valid action"), "Default actions should not do ambiguous actions");

        response = sendCommandToServer("steve: drop");
        assertTrue(response.toLowerCase().contains("no valid action"), "Default actions should not do ambiguous actions");

        response = sendCommandToServer("steve: get axe potion");
        assertTrue(response.toLowerCase().contains("no valid action"), "Default actions should not do ambiguous actions");

        sendCommandToServer("steve: get axe");
        sendCommandToServer("steve: get potion");

        response = sendCommandToServer("steve: drop axe potion");
        assertTrue(response.toLowerCase().contains("no valid action"), "Default actions should not do ambiguous actions");

        sendCommandToServer("steve: drop axe");
        sendCommandToServer("steve: drop potion");
    }

    @Test
    void testMultipleSimilarActions(){
        // A command should contain only one action. It can contain multiple action phrases of the same action.
        // It cannot contain action phrases of different actions. i.e. if "cut" == "cut down", it's okay, else it's not.

        sendCommandToServer("steve: goto forest");
        sendCommandToServer("steve: get key");
        sendCommandToServer("steve: goto cabin");

        String response = sendCommandToServer("steve: open trapdoor");

        assertTrue(response.toLowerCase().contains("trapdoor"), "Should open the trapdoor");

    }

    @Test
    void testMultipleDifferentActions(){
        // A command should contain only one action. It can contain multiple action phrases of the same action.
        // It cannot contain action phrases of different actions. i.e. if "cut" == "cut down", it's okay, else it's not.

        sendCommandToServer("steve: goto forest");
        sendCommandToServer("steve: get key");

        String response = sendCommandToServer("steve: goto cabin open trapdoor");

        assertTrue(response.toLowerCase().contains("no valid"), "Should not open the trapdoor");

    }

    @Test
    void testMultiPlayer() {
        sendCommandToServer("steve: get axe");
        String response = sendCommandToServer("bob: inv");
        assertFalse(response.toLowerCase().contains("axe"), "Bob should not have Steve's axe");

        sendCommandToServer("bob: get potion");
        response = sendCommandToServer("steve: inv");
        assertFalse(response.toLowerCase().contains("potion"), "Steve should not have Bob's potion");

        response = sendCommandToServer("steve: look");
        assertTrue(response.toLowerCase().contains("bob"), "Steve should see Bob in the room");

        response = sendCommandToServer("bob: look");
        assertTrue(response.toLowerCase().contains("steve"), "Bob should see Steve in the room");
    }
}
