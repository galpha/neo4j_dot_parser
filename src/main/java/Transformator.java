

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * Transformiert auf neo4j basierende GraphDataSets in .DOT format (ohne
 * subgraph)
 *
 * @author gomezk
 */
public class Transformator extends Thread {


  private String BTG_ID;
  private GraphDatabaseService graphDB;


  public Transformator(GraphDatabaseService graphDB, String btg) {
    this.graphDB = graphDB;
    this.BTG_ID = btg;
    //start();

  }


  /**
   * Ordnet allen BTG's die dazugehörigen Knoten zu und speichert alles in der
   * completemap
   *
   * @param BTG Property zur zuordnung der Knoten
   * @return
   */

  public Map<Long, List<Long>> prepareData() {
    Map<Long, List<Long>> btgNodesMap = new HashMap<>();
    try (Transaction tx = graphDB.beginTx()) {
      for (Node n : GlobalGraphOperations.at(graphDB).getAllNodes()) {
        if (n.hasProperty(BTG_ID)) {
          Long btgID = (Long) n.getProperty(BTG_ID);
          if (btgNodesMap.containsKey(btgID)) {
            btgNodesMap.get(btgID).add(n.getId());
          } else {
            List<Long> nodes = new ArrayList<>();
            nodes.add(n.getId());
            btgNodesMap.put(btgID, nodes);
          }
        }
      }
      tx.success();
    }
    return btgNodesMap;
  }

}
