package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.actions.*;
import edu.uob.entities.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.logging.Logger;

import static edu.uob.Config.DEFAULT_TEXT;

public final class GameServer {
    private static final Logger logger = Logger.getLogger(GameServer.class.getName());
    private static final char END_OF_TRANSMISSION = 4;
    private final EntityRegistrar entityRegistrar;
    private final ActionRegistrar actionRegistrar;
    private final Location storeroom;
    private final Map<String, NPC> npcSet;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instantiates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {

        // Parsing entity file
        Parser entityParser = new Parser();
        try {
            FileReader in = new FileReader(entitiesFile);
            entityParser.parse(in);
        } catch (FileNotFoundException e) {
            logger.severe("Entities Config file not found!\n" + e);
        } catch (ParseException e) {
            logger.severe("Entities Config file is corrupted!\n" + e);
        }

        ArrayList<Graph> graphs = entityParser.getGraphs();
        entityRegistrar = new EntityRegistrar();
        try {
            entityRegistrar.createEntities(graphs);
        } catch (IndexOutOfBoundsException e) {
            logger.severe(e.toString());
        }

        // Caching storeroom, NPCs
        storeroom = entityRegistrar.getLocations().getLocation("storeroom");
        npcSet = entityRegistrar.getNPCs();

        // Parsing actions file
        Document document = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(actionsFile);
        } catch(ParserConfigurationException pce) {
            logger.severe("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch(SAXException saxe) {
            logger.severe("SAXException was thrown when attempting to read actions file");
        } catch(IOException ioe) {
            logger.severe("IOException was thrown when attempting to read actions file");
        } catch(IllegalArgumentException iae) {
            logger.severe("IllegalArgumentException was thrown when attempting to read actions file");
        }

        Element root = document.getDocumentElement();

        NodeList actions = root.getElementsByTagName("action");

        actionRegistrar = new ActionRegistrar();
        actionRegistrar.createActions(actions);

    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {

        // Flow
        // 0. Check for player, create new if needed
        // 1. Parser - split, remove duplicates, lowercase and return list of tokens
        // 2. Tagger - tags all tokens based on the registry and returns a map
        // 3. Validator - Validates if the actions' subjects' and other needs are there
        // 4. Orchestrate - calls the actions

        // 0.
        String[] commands = command.split(":",2);
        String playerName = commands[0];
        if(!entityRegistrar.getPlayers().containsKey(playerName)) {
            entityRegistrar.registerPlayer(playerName);
        }
        Player player = entityRegistrar.getPlayers().get(playerName);

        // 1.
        command = commands[1];
        Set<String> tokens = CommandParser.parse(command);

        // 2.
        Map<String, Set<String>> taggedTokens = Tagger.tag(tokens, entityRegistrar, actionRegistrar);

        // 3.
        boolean containsDefault = false;
        Set<String> actionTokens = taggedTokens.get("actions");
        if (actionTokens != null) {
            for (String action : actionTokens) {
                for (DefaultActions defaultAction : DefaultActions.values()) {
                    if (action.equalsIgnoreCase(defaultAction.name())) {
                        containsDefault = true;
                        break;
                    }
                }
                if (containsDefault) break;
            }
        }

        if (containsDefault) {
            if(Validator.validateDefaultAction(taggedTokens, actionRegistrar)) {
                String actionPhrase = actionTokens.iterator().next();
                DefaultActions action = DefaultActions.valueOf(actionPhrase.toUpperCase());
                Set<String> subjectTokens = taggedTokens.get("subjects");
                String subjectPhrase = subjectTokens == null || subjectTokens.isEmpty() ? "" : subjectTokens.iterator().next();
                // 4.
                return switch (action) {
                    case LOOK -> Look.look(player);
                    case INV, INVENTORY -> Inventory.inv(player);
                    case HEALTH -> Health.health(player);
                    case GET -> Get.get(subjectPhrase, player);
                    case DROP -> Drop.drop(subjectPhrase, player);
                    case GOTO -> GoTo.goTo(subjectPhrase, player);
                };

            } else {
                return DEFAULT_TEXT;
            }
        }

        GameAction gameAction;
        gameAction = Validator.validate(taggedTokens, actionRegistrar);
        if(gameAction == null) {
            return DEFAULT_TEXT;
        }
        return gameAction.doAction(storeroom, player, npcSet);

    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
