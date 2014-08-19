package data.process;

import data.ExcelToEntityParser;
import jura.network.bus.entity.neo4j.Locality;
import jura.network.bus.entity.neo4j.Relation;
import jura.network.bus.entity.neo4j.Station;
import jura.network.bus.neo4j.EntityLabels;
import jura.network.bus.neo4j.EntityProperties;
import jura.network.bus.neo4j.EntityRelationshipType;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import jura.network.bus.services.TudGraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(Neo4jFiler.class);

    public Neo4jFiler(){
        parser = ExcelToEntityParser.getInstance().startParse();
        service = TudGraphService.getTudDataBaseService();
    }

    public void start(){
        this.clearDb();
        this.fillStations();
        this.fillRelations();
        this.showNumberOfNodeAsLog();
        logger.info("Inserting db done!");
    }


    public static void main(String[] args) {

        Neo4jFiler filer = new Neo4jFiler();
        filer.start();
    }

    private void fillRelations(){

        logger.info("Starting filling neo4j db --> filling relations...");

        relations = parser.relations();

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
        logger.info("Ending filling neo4j db --> filling relations, ok");

    }

    private void createStationConnection(Node stationDepart, Node stationArrivee, Relation relation){

        Relationship departToArrivee = stationDepart.createRelationshipTo(stationArrivee, EntityRelationshipType.IS_CONNECTED);
        departToArrivee.setProperty(EntityProperties.LIGNE.propertyName,relation.getLigne());
        departToArrivee.setProperty(EntityProperties.COMPAGNIE.propertyName,relation.getCompagnie());
        departToArrivee.setProperty(EntityProperties.DUREE.propertyName,relation.getDuree());
        logger.info("Inserting relation: " + stationDepart + " -> " + stationArrivee);
    }

    private Station getStationFromMapById(Station stat){

        for(Station station : stationsByLocalities.get(stat.getLocality())){
            if(stat.equals(station)){
                return station;
            }
        }

        logger.error("Error, station : " + stat + "not found in the list");

        return null;
    }

    private  void fillStations(){

        logger.info("Starting filling neo4j db --> filling stations...");

        stationsByLocalities = parser.stationsByLocalite();

        try ( Transaction tx = service.beginTx() )
        {
            for(Locality localite:stationsByLocalities.keySet()){
                Node loca = service.createNode();
                loca.addLabel(EntityLabels.Localite.label);
                loca.setProperty(EntityProperties.NOM.propertyName,localite.getNom());

                localite.setNeo4jId(loca.getId());

                logger.info("Inserting locality: " + localite);

                for(Station station:stationsByLocalities.get(localite)){
                    Node stat = service.createNode();
                    stat.addLabel(EntityLabels.Station.label);
                    stat.setProperty(EntityProperties.NOM.propertyName,station.getNom());
                    stat.setProperty(EntityProperties.LATITUDE.propertyName,station.getLatitude());
                    stat.setProperty(EntityProperties.LONGITUDE.propertyName,station.getLongitude());

                    station.setNeo4jId(stat.getId());

                    logger.info("Inserting station: " + station);

                    Relationship statToLoca = stat.createRelationshipTo(loca, EntityRelationshipType.IS_LOCATED);

                    logger.info("Inserting relationship: (" + stat + ")-(" + loca + ")");
                }
            }
            tx.success();


        }

    }

    void clearDb() {

        logger.info("Clearing db.....");

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

        logger.info("DB Cleared ok");
        showNumberOfNodeAsLog();
    }

    void showNumberOfNodeAsLog()  {

        String numberOfNodeQuery = "Match n Return Count(n)";

        try(Transaction tx = service.beginTx()){
            ExecutionResult result;
            ExecutionEngine engine = new ExecutionEngine(service);
            result = engine.execute(numberOfNodeQuery);
            logger.info("Number of actual nodes: ");

            String neoLog = new String(result.dumpToString());

            for(String line : neoLog.split("\\r?\\n")){
                logger.info(line);
            }



            tx.success();
        }



    }
}
