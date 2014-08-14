package data;

/**
 * Created by sce on 14.08.14.
 */
public enum TudProperty {

    LIGNE("line"), COMPAGNIE("company"), NOM("name"), DUREE ("duration");

    public String propertyName;

    TudProperty(String property){
        this.propertyName = property;
    }
}
