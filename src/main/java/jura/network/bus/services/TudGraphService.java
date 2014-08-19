package jura.network.bus.services;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import jura.network.bus.config.Config;

/**
 * Return the services to communicate with neo4j embeded db
 */
public class TudGraphService {

    public static GraphDatabaseService getTudDataBaseService(){
        return new GraphDatabaseFactory().newEmbeddedDatabase(Config.EMBED_NEO4J_DB_PATH.value);
    }
}
