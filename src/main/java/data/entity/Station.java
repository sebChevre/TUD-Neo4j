package data.entity;

/**
 * Created by sce on 11.08.14.
 */
public class Station extends Neo4jEntity{

    private Locality locality;
    private String nom;

    public Station(Locality locality, String nom){
        this.locality = locality;
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public Locality getLocality() {
        return locality;
    }

    @Override
    public String toString() {
        return "Station{" +
                "locality='" + locality + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;

        Station station = (Station) o;

        if (!locality.equals(station.locality)) return false;
        if (!nom.equals(station.nom)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locality.hashCode();
        result = 31 * result + nom.hashCode();
        return result;
    }

    public static Station parse(String ligne){

        Station station;

        //Si que localité, localité et nom de station idem
        if(ligne.split(",").length == 1){
            Station st = new Station(new Locality(ligne.split(",")[0].trim()), ligne.split(",")[0].trim());
            return st;
        }else{
            Station st = new Station(new Locality(ligne.split(",")[0].trim()),ligne.split(",")[0].toString()+", "+ligne.split(",")[1].trim());
            return st;
        }
    }
}
