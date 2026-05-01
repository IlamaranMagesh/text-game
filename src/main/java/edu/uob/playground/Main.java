package edu.uob.playground;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;import com.alexmerz.graphviz.objects.Id;
import edu.uob.actions.ActionRegistrar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws ParseException, ParserConfigurationException, IOException, SAXException {
        File entitiesFile = Paths.get("Weekly Workbooks/10 STAG Assignment/resources/cw-stag/config" + File.separator + "basic-entities.dot")
                .toAbsolutePath().toFile();
        Parser entityParser = new Parser();

        try {
            FileReader in = new FileReader(entitiesFile);
            entityParser.parse(in);
        } catch (FileNotFoundException e) {
            logger.severe("Entities Config file not found: " + e);
        } catch (ParseException e) {
            System.out.println("Entities Config file is corrupted!\n" + e);
        }

        ArrayList<Graph> graphs = entityParser.getGraphs();
        Graph locations = graphs.get(0).getSubgraphs().get(0);
        Graph paths = graphs.get(0).getSubgraphs().get(1);

        Graph location1 = locations.getSubgraphs().get(0);
//        System.out.println(location1.getNodes(true).get(0).getAttribute("descriptio"));


        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse("Weekly Workbooks/10 STAG Assignment/resources/cw-stag/config" + File.separator + "basic-actions.xml");
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();

//        NodeList actions = root.getElementsByTagName("action");

        ActionRegistrar a = new ActionRegistrar();
        a.createActions(actions);
        System.out.println(actions.item(1));

        for(int i = 0; i < actions.getLength(); i++) {
            Node action = actions.item(i);
            if(action.getNodeType() == Node.ELEMENT_NODE) {
                Element actionElement = (Element) action;
                NodeList subjects = actionElement.getElementsByTagName("triggers");

              for(int j = 0; j < subjects.getLength(); j++) {
                  Node subject = subjects.item(j);
                  if(subject.getNodeType() == Node.ELEMENT_NODE) {
                      Element subjectElement = (Element) subject;
                      NodeList entities = subjectElement.getElementsByTagName("keyphrase");
                      for(int k = 0; k < entities.getLength(); k++) {
                         System.out.println(entities.item(k).getTextContent());
                      }
                  }
              }
            }}

//        String s = "asd ! asdq12!, asd! simon:";
//        String a = "simon: asdasde";
//        s = s.replaceAll("[^a-z]+", " ");
//        String tokens = s.replaceAll("[\\s]{2,}", " ");
//
//        Set<String> set = new LinkedHashSet<>(Arrays.asList(tokens));

//        Map<String, Set<String>> taggedTokens = new HashMap<>();


        System.out.println();







//        location1.getNodes(true).forEach(System.out::println);
//
//        Graph artefacts = location1.getSubgraphs().get(0);
//        System.out.println(artefacts.getNodes(true).get(0).getId());

//        StringBuffer graph = new StringBuffer("digraph layout {\n" +
//                "    splines=ortho;\n" +
//                "    node [shape=\"rect\"];\n" +
//                "    subgraph cluster001 {\n" +
//                "        node [shape=\"none\"];\n" +
//                "        cabin [description=\"A log cabin in the woods\"];\n" +
//                "        subgraph artefacts {\n" +
//                "            node [shape=\"diamond\"];\n" +
//                "            potion [description=\"A bottle of magic potion\"];\n" +
//                "            axe [description=\"A razor sharp axe\"];\n" +
//                "            coin [description=\"A silver coin\"];\n" +
//                "        }\n" +
//                "    }\n" +
//                "}");
//
//        Parser p = new Parser();
//        p.parse(graph);
//        System.out.println(p.getGraphs().get(0));


    }
}
