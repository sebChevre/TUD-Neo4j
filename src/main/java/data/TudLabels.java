package data;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

/**
 * Created by seb on 12.08.14.
 */
public enum TudLabels {

    Localite(DynamicLabel.label("Locality")),
    Station(DynamicLabel.label("Station"));

    public Label label;

    TudLabels(Label label){
        this.label = label;
    }
}
