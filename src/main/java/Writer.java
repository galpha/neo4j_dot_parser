
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author gomezk
 */
public class Writer {

    private GraphDatabaseService graphDB;
    private String outputFilePath;
    private String btg;
    private Transformator trans;
    private BufferedWriter fileWriter;
    private String PATH;

    // DOT specific patterns
    private static final String DOT_DIGRAPH_HEADER = "digraph";
    private static final String DOT_BLOCK_OPEN = "{";
    private static final String DOT_BLOCK_CLOSE = "}";
    private static final String DOT_OUT_EDGE = "->";
    private static final String DOT_LABEL_OPEN = "[label=\"";
    private static final String DOT_LABEL_CLOSE = "\"]";
    private static final String DOT_LINE_ENDING = ";";

    public Writer(GraphDatabaseService graphdb, String dateiname, String btg, String Path) {
        this.graphDB = graphdb;
        this.outputFilePath = dateiname;
        this.btg = btg;
        this.PATH = Path;

    }

    public void write() {
        try {
            Transformator trans = new Transformator(graphDB, btg);
            setup();
            writeDigraph(trans.prepareData());
            teardown();
        } catch (IOException ex) {
            System.out.print(ex);
        }
    }


    private void writeDigraph(Map<Long, List<Long>> btgNodesMap) throws IOException {
        try (Transaction tx = graphDB.beginTx()) {
            for (Map.Entry<Long, List<Long>> btgWithNodes : btgNodesMap.entrySet()) {
                // digraph header
                fileWriter.write(String.format("%s %d %s", DOT_DIGRAPH_HEADER, btgWithNodes.getKey(), DOT_BLOCK_OPEN));
                fileWriter.newLine();
                // nodes
                writeNodes(btgWithNodes.getValue());
                // edges
                writeEdges(btgWithNodes.getValue());
                // digraph footer
                fileWriter.write(DOT_BLOCK_CLOSE);
                fileWriter.newLine();
            }
            tx.success();
        }
    }

    private void writeNodes(List<Long> nodes) throws IOException {
        for (Long nodeID : nodes) {
            Node currentNode = graphDB.getNodeById(nodeID);
            String nodeLabel = currentNode.getLabels().toString();
            // writes
            // 0 [label="ERP_xy"];
            fileWriter.write(String.format("\t%d %s%s%s%s",
                    currentNode.getId(),
                    DOT_LABEL_OPEN,
                    nodeLabel,
                    DOT_LABEL_CLOSE,
                    DOT_LINE_ENDING));
            fileWriter.newLine();
        }
    }

    private void writeEdges(List<Long> nodes) throws IOException {
        for (Long nodeID : nodes) {
            Node currentNode = graphDB.getNodeById(nodeID);
            for (Relationship edge : currentNode.getRelationships(Direction.OUTGOING)) {
                // writes
                // 0 -> 1 [label="edgeLabel"];
                fileWriter.write(String.format("\t%d %s %d %s%s%s%s",
                        edge.getStartNode().getId(),
                        DOT_OUT_EDGE,
                        edge.getEndNode().getId(),
                        DOT_LABEL_OPEN,
                        edge.getType().name(),
                        DOT_LABEL_CLOSE,
                        DOT_LINE_ENDING));
                fileWriter.newLine();
            }
        }
    }

    private void setup() throws IOException {
        this.fileWriter = new BufferedWriter(new FileWriter(outputFilePath));
    }

    private void teardown() throws IOException {
        this.graphDB.shutdown();
        this.fileWriter.close();
    }


}
