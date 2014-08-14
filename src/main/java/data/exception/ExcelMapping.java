package data.exception;

/**
 * Created by sce on 14.08.14.
 */
public enum ExcelMapping {

    LIGNE(0),
    STATION_DEPART(1),
    STATION_ARRIVE(2),
    IS_BIDIRECTIONNAL(3),
    COMPAGNIE(4),
    DUREE(5);

    public int coloneNo;

    ExcelMapping(int coloneNo){
        this.coloneNo = coloneNo;
    }
}
