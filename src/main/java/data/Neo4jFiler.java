package data;

import data.entity.Locality;
import data.entity.Relation;
import data.entity.Station;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import tud.service.TudGraphService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by seb on 13.08.14.
 */
public class Neo4jFiler {


    private ExcelToEntityParser parser = null;
    private GraphDatabaseService service = null;
    private Map<Locality,List<Station>> stationsByLocalities;
    private List<Relation> relations = null;

    public Neo4jFiler(){
        parser = ExcelToEntityParser.getInstance();
        service = TudGraphService.getTudDataBaseService();
    }

    public void start(){
        this.clearDb();
        parser.startParse();
        this.fillStations();
        this.fillRelations();
    }


    public static void main(String[] args) {

        Neo4jFiler filer = new Neo4jFiler();
        filer.start();
    }

    private void fillRelations(){

        System.out.println("Starting filling neo4j db --> filling stations");

        relations = parser.getRelations();

        try ( Transaction tx = service.beginTx() ){

            for(Relation relation:relations){

                Station dep = getStationFromMapById(relation.getStationDepart());
                Station arr = getStationFromMapById(relation.getStationArrivee());

                Node stationDepart = service.getNodeById(dep.getNeo4jId());
                Node stationArrivee = service.getNodeById(arr.getNeo4jId());

                createStationConnection(stationDepart,stationArrivee,relation);


                if(relation.isBiderectionnal()){
                    createStationConnection(stationArrivee,stationDepart,relation);
                }
            }

            tx.success();

        }
    }

    private void createStationConnection(Node stationDepart, Node stationArrivee, Relation relation){
        Relationship departToArrivee = stationDepart.createRelationshipTo(stationArrivee,TudRelationShipType.IS_CONNECTED);
        departToArrivee.setProperty(TudProperty.LIGNE.propertyName,relation.getLigne());
        departToArrivee.setProperty(TudProperty.COMPAGNIE.propertyName,relation.getCompagnie());
        departToArrivee.setProperty(TudProperty.DUREE.propertyName,relation.getDuree());
    }

    private Station getStationFromMapById(Station stat){

        for(Station station : stationsByLocalities.get(stat.getLocality())){
            if(stat.equals(station)){
                return station;
            }
        }

        System.out.println("Error, station : " + stat + "not found in the list");

        return null;
    }

    private  void fillStations(){

        System.out.println("Starting filling neo4j db --> filling stations");

        stationsByLocalities = parser.getStationsByLocalite();

        try ( Transaction tx = service.beginTx() )
        {
            for(Locality localite:stationsByLocalities.keySet()){
                Node loca = service.createNode();
                loca.addLabel(TudLabels.Localite.label);
                loca.setProperty(TudProperty.NOM.propertyName,localite.getNom());

                localite.setNeo4jId(loca.getId());

                System.out.println("Inserting locality: " + localite);

                for(Station station:stationsByLocalities.get(localite)){
                    Node stat = service.createNode();
                    stat.addLabel(TudLabels.Station.label);
                    stat.setProperty(TudProperty.NOM.propertyName,station.getNom());

                    station.setNeo4jId(stat.getId());

                    System.out.println("Inserting station: " + station);

                    Relationship statToLoca = stat.createRelationshipTo(loca,TudRelationShipType.IS_LOCATED);

                    System.out.println("Inserting relationship: (" + stat +")-(" + loca +")");
                }
            }
            tx.success();

            System.out.println("Inserting dn done!");
        }

    }

    void clearDb() {

        System.out.println("Clearing db.....");

        String clearDbQuery = "MATCH (n)\n" +
                "OPTIONAL MATCH (n)-[r]-()\n" +
                "DELETE n,r";

        showNumberOfNodeAsLog();

        try(Transaction tx = service.beginTx()){
            ExecutionResult result;
            ExecutionEngine engine = new ExecutionEngine(service);
            result = engine.execute(clearDbQuery);

            tx.success();
        }

        System.out.println("DB Cleared ok");
        showNumberOfNodeAsLog();
    }

    void showNumberOfNodeAsLog()  {

        String numberOfNodeQuery = "Match n Return Count(n)";

        try(Transaction tx = service.beginTx()){
            ExecutionResult result;
            ExecutionEngine engine = new ExecutionEngine(service);
            result = engine.execute(numberOfNodeQuery);
            System.out.println("Number of actual nodes: " + result.dumpToString());

            tx.success();
        }



    }
}
