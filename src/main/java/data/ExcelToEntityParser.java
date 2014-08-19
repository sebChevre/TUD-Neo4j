package data;


import jura.network.bus.config.Config;
import jura.network.bus.entity.neo4j.Locality;
import jura.network.bus.entity.neo4j.Relation;
import jura.network.bus.entity.neo4j.Station;
import jura.network.bus.exception.DataCoherenceException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 * Classe parsant le fichier excel contenant les données à importer
 *
 */
public class ExcelToEntityParser {

    private Config xlsFile = Config.EXCEL_DATA_FILE;
    private Map<Locality,List<Station>> stationsByLocalite = null;
    private List<Relation> relations = null;
    private boolean isStartParseCalled = false;
    private final Logger logger = LoggerFactory.getLogger(ExcelToEntityParser.class);

    /**
     * Retourne une instance. Instancie les objets minimaux nécessaire sau traitement du fichier excel
     * @return l'instance une fois le traitement ok
     */
    public static ExcelToEntityParser getInstance(){

        ExcelToEntityParser parser = null;

        parser = new ExcelToEntityParser();

        return parser;
    }

    public ExcelToEntityParser startParse(){
        logger.info("Starting parsing excel file [" + xlsFile.value + "]");

        this.parseStations();
        this.parseRelations();
        isStartParseCalled = true;

        logger.info("Excel file parsing succesfully finished");
        return this;
    }

    public ExcelToEntityParser configureXlsFile(Config file){
        this.xlsFile = file;
        logger.info("Excel source file modified: " + file.value);
        return this;
    }

    /**
     * Retourne le classeur avec le chemin de fichier passé en paramètre
     * @return une instance de workbook, correspondant au fichier excel
     * @throws java.io.IOException si problème avec le chargement du fichier
     */
    private HSSFWorkbook getWorkBookForSheet(){
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        try {
            fs = new POIFSFileSystem(new FileInputStream(xlsFile.value));
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
           logger.error("Exception during retrieving workbook [" + xlsFile.value + "]");
           logger.error(e.getMessage());
        }
        return wb;
    }


    /**
     * Retourne la map associatipm Localite->Stations
     * @return la map
     */
    public Map<Locality, List<Station>> stationsByLocalite() {
        checkStartParseCall();
        return Collections.unmodifiableMap(stationsByLocalite);
    }


    private void checkStartParseCall(){
        if(!this.isStartParseCalled){
            throw new UnsupportedOperationException("The method startParse need to be called before accessing collections");
        }
    }
    /**
     * Retourne les relations sous forme de liste
     * @return une liste des relations
     */
    public List<Relation> relations() {
        checkStartParseCall();
        return Collections.unmodifiableList(relations);
    }

    /**
     * Retourne les stations sous forme de liste
     * @return une liste des stations
     */
    public List<Station> stations(){
        checkStartParseCall();
        List<Station> stations  = new ArrayList<>();

        for(List<Station> stationsByLocality : stationsByLocalite.values()){
            stations.addAll(stationsByLocality);
        }

        return stations;
    }

    public List<Locality> localites(){
        checkStartParseCall();
        List<Locality> stations  =  new ArrayList<>();
        stations.addAll(stationsByLocalite.keySet());

        return stations;
    }

    private void parseStations() {

        HSSFSheet sheet = this.getWorkBookForSheet().getSheetAt(Config.STATIONS_LOCATIONS_SHEET.intValue());
        HSSFRow row;
        HSSFCell cell;
        Iterator<Row> rowIterator = sheet.iterator();

        //Exclusion première ligne
        boolean firstLine = true;
        //instanciation map
        stationsByLocalite = new HashMap<>();


        logger.info("Starting stations parsing, filling map stations by localities, with sheet: "
                + Config.STATIONS_LOCATIONS_SHEET.intValue() +", in this workbook: " + this.xlsFile.value);

        while(rowIterator.hasNext()){
            Row ligne = rowIterator.next();

            if(!firstLine){
                String line = ligne.getCell(0).getStringCellValue();
                Locality locality = Locality.parse(line);
                Station station = Station.parse(line,ligne.getCell(1).getNumericCellValue(),ligne.getCell(2).getNumericCellValue());

                if(!locality.equals(station.getLocality())){
                    throw new DataCoherenceException("The localities seems to be corrupted: [" + locality +", "
                            + station.getLocality());
                }

                if(stationsByLocalite.containsKey(locality)){
                    stationsByLocalite.get(locality).add(station);
                }else{
                    List<Station> stations = new ArrayList<>();
                    stations.add(station);
                    stationsByLocalite.put(locality,stations);
                }

            }

            firstLine = false;

        }


        int stationsCount = 0;
        for(Locality localite: stationsByLocalite.keySet()){
            for(Station station:stationsByLocalite.get(localite)){
                stationsCount++;
            }
        }
        logger.info("Station parsing ending. " + stationsByLocalite.keySet().size() + " localities "
            +" ," + stationsCount + " stations");

    }

    private void parseRelations() {

        HSSFSheet sheet = getWorkBookForSheet().getSheetAt(Config.RELATIONS_SHEET.intValue());
        HSSFRow row;
        HSSFCell cell;
        Iterator<Row> rowIterator = sheet.iterator();
        boolean firstLine = true;
        relations = new ArrayList<>();

        logger.info("Starting relations parsing, filling list relation, with sheet: "
                + Config.RELATIONS_SHEET.intValue() + ", in this workbook: " + this.xlsFile.value);


        while(rowIterator.hasNext()){
            Row ligne = rowIterator.next();

            //On ne traite pas la premiere ligne --> en tete
            if(!firstLine){

                Relation relation = Relation.parseWithExcelRow(ligne);
                relations.add(relation);

            }

            firstLine = false;

        }

        logger.info("Relation parsing ending. " + relations.size() + " relations");

    }


}
