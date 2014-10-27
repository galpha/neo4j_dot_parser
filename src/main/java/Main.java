

import java.util.ArrayList;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * @author galpha
 */
public class Main {


    private static String path = "/home/gomezk/testdb/data2.db";//Pfad zur DB
    private static String btg = "_BTG";//Name der Property
    private static String dateiname = "100";//Name des output files ".DOT" wird auto. angehangen
    private static GraphDatabaseService graphdb;
    private static Map<String, ArrayList<Node>> compmap;

    public static void main(final String[] args) throws InterruptedException {
        // comment
        Connection con = new Connection(path);
        graphdb = con.setConnection();


        Writer write = new Writer(graphdb, dateiname, btg, path);
        write.write();

        con.shutDown(graphdb);
    }

}
