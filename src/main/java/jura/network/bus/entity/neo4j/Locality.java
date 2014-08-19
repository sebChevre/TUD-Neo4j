package jura.network.bus.entity.neo4j;

/**
 * Created by sce on 11.08.14.
 */
public class Locality extends Neo4jEntity{

    private String nom;
    private Double latitude;
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Locality(String nom){
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return "Locality{" +
                "nom='" + nom + '\'' +
                '}';
    }

    public static Locality parse(String ligne){
        String localiteName = ligne.split(",")[0];
        return new Locality(localiteName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Locality)) return false;

        Locality locality = (Locality) o;

        if (!nom.equals(locality.nom)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return nom.hashCode();
    }
}
