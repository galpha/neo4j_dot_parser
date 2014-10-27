import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author gomezk
 */
public class Connection {

    private GraphDatabaseService graphdb;
    private String path;

    public Connection(String PATH){
        this.path=PATH;
    }

    public GraphDatabaseService setConnection(){
        graphdb = new GraphDatabaseFactory().newEmbeddedDatabase(path);
        System.out.println("Connected");
        return graphdb;
    }


    public void shutDown(GraphDatabaseService graphDB){
        graphDB.shutdown();
        //System.out.println("closed");
    }
}
