package embed;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import tud.service.TudGraphService;

import java.io.IOException;
import java.util.List;

/**
 * Created by sce on 04.08.14.
 */
public class PopulateDb {


    public static GraphDatabaseService service = null;

    public static void main(String[] args) throws IOException {

        initService();
        //populate();
        //clearDb();

        showNumberOfNodeAsLog();
    }


    static void initService(){
        if(service == null){
            service = TudGraphService.getTudDataBaseService();
        }
    }

    static void populate(){
        List<String> populateDbQuery = Util.readAllFiles();

        //showNumberOfNodeAsLog();

        int filecount = 1;

        for(String script:populateDbQuery){

            System.out.println("Populate db, file: " + filecount);

            try(Transaction tx = service.beginTx()){
                ExecutionResult result;
                ExecutionEngine engine = new ExecutionEngine(service);
                result = engine.execute(script);

                tx.success();
            }

            System.out.println("Cypher script ok for file n° : " + filecount);
            filecount++;
        }


        //showNumberOfNodeAsLog();

    }

    static void clearDb() throws IOException {
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

        showNumberOfNodeAsLog();


    }

    static void showNumberOfNodeAsLog()  {

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
