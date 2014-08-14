package tud.service;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import data.Config;

/**
 * Created by sce on 04.08.14.
 */
public class TudGraphService {

    public static GraphDatabaseService getTudDataBaseService(){
        return new GraphDatabaseFactory().newEmbeddedDatabase(Config.EMBED_GRAPH_DB_PATH.value);
    }
}
