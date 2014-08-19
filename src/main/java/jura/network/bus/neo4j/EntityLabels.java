package jura.network.bus.neo4j;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

/**
 * Created by seb on 12.08.14.
 */
public enum EntityLabels {

    Localite(DynamicLabel.label("Locality")),
    Station(DynamicLabel.label("Station"));

    public Label label;

    EntityLabels(Label label){
        this.label = label;
    }
}
