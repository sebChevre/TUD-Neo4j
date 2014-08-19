package jura.network.bus.entity.neo4j;

import jura.network.bus.config.ExcelMapping;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by seb on 11.08.14.
 */
public class Relation {

    private Station stationDepart;
    private Station stationArrivee;
    private boolean isBiderectionnal;
    private String compagnie;
    private String ligne;
    private double duree;

    public Relation(Station stationDepart,Station stationArrivee,boolean isBiderectionnal,String compagnie,String ligne, double duree){
        this.stationDepart = stationDepart;
        this.stationArrivee = stationArrivee;
        this.isBiderectionnal = isBiderectionnal;
        this.compagnie = compagnie;
        this.ligne = ligne;
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "stationDepart=" + stationDepart +
                ", stationArrivee=" + stationArrivee +
                ", isBiderectionnal=" + isBiderectionnal +
                '}';
    }

    public Station getStationDepart() {
        return stationDepart;
    }

    public Station getStationArrivee() {
        return stationArrivee;
    }

    public boolean isBiderectionnal() {
        return isBiderectionnal;
    }

    public String getCompagnie() {
        return compagnie;
    }

    public String getLigne() {
        return ligne;
    }

    public double getDuree() {
        return duree;
    }

    public static Relation parseWithExcelRow(Row ligne){
        String lineName = ligne.getCell(ExcelMapping.LIGNE.coloneNo).getStringCellValue();
        String stationStart = ligne.getCell(ExcelMapping.STATION_DEPART.coloneNo).getStringCellValue();
        String stationEnd = ligne.getCell(ExcelMapping.STATION_ARRIVE.coloneNo).getStringCellValue();
        String isBidirectionnal = ligne.getCell(ExcelMapping.IS_BIDIRECTIONNAL.coloneNo).getStringCellValue();
        String compagnie = ligne.getCell(ExcelMapping.COMPAGNIE.coloneNo).getStringCellValue();
        boolean isBidrectionnalAsBoolean = (isBidirectionnal.equals("1"));
        double duree = Double.parseDouble(ligne.getCell(ExcelMapping.DUREE.coloneNo).getStringCellValue());

        Station depart = Station.parse(stationStart);
        Station arrivee = Station.parse(stationEnd);

        return new Relation(depart,arrivee,isBidrectionnalAsBoolean,compagnie,lineName,duree);
    }
}
