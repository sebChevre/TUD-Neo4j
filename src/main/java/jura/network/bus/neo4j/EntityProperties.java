package jura.network.bus.neo4j;

/**
 * Created by sce on 14.08.14.
 */
public enum EntityProperties {

    LIGNE("line"), COMPAGNIE("company"), NOM("name"), DUREE ("duration"), LATITUDE("latitude"),LONGITUDE("longitude");

    public String propertyName;

    EntityProperties(String property){
        this.propertyName = property;
    }
}
