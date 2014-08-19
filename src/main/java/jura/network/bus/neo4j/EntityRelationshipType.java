package jura.network.bus.neo4j;


import org.neo4j.graphdb.RelationshipType;

/**
 * Created by sce on 12.08.14.
 */
public enum EntityRelationshipType implements RelationshipType {

    IS_LOCATED,IS_CONNECTED;

}
