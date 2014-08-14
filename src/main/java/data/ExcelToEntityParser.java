package data;


import data.entity.Locality;
import data.entity.Relation;
import data.entity.Station;
import data.exception.DataCoherenceException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 * Classe parsant le fichier excel contenant les données à importer
 *
 */
public class ExcelToEntityParser {


    private POIFSFileSystem fs;
    private HSSFWorkbook wb;
    private Map<Locality,List<Station>> stationsByLocalite = null;
    private List<Relation> relations = null;

    /**
     * Retourne une instance. INstancie les objets minimaux nécessaire sau traitement du fichier excel
     * @return
     */
    public static ExcelToEntityParser getInstance(){

        ExcelToEntityParser parser = null;
        try {
            parser = new ExcelToEntityParser();
            parser.fs = new POIFSFileSystem(new FileInputStream(Config.EXCEL_DATA_FILE.value));
            parser.wb = new HSSFWorkbook(parser.fs);
        } catch (IOException e){
            System.out.println("Error initialisation - " + ExcelToEntityParser.class.getName());
            e.printStackTrace();
        }

        return parser;
    }

    /**
     * Démmarage du parsing global
     */
    public void startParse(){
        parseStations();
        parseRelations();
    }

    public Map<Locality, List<Station>> getStationsByLocalite() {
        return Collections.unmodifiableMap(stationsByLocalite);
    }

    public List<Relation> getRelations() {
        return Collections.unmodifiableList(relations);
    }


    private void parseStations() {
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row;
        HSSFCell cell;
        Iterator<Row> rowIterator = sheet.iterator();
        boolean firstLine = true;
        stationsByLocalite = new HashMap<>();

        System.out.println("Starting stations parsing, filling map stations by localities....");

        while(rowIterator.hasNext()){
            Row ligne = rowIterator.next();

            if(!firstLine){
                String line = ligne.getCell(0).getStringCellValue();
                Locality locality = Locality.parse(line);
                Station station = Station.parse(line);

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

        System.out.println("Station parsing ending. " + stationsByLocalite.keySet().size() + " locallities");
        int stationsCount = 0;
        for(Locality localite: stationsByLocalite.keySet()){
            for(Station station:stationsByLocalite.get(localite)){
                stationsCount++;
            }
        }
        System.out.println("Station parsing ending. " + stationsCount + " stations");

    }

    private void parseRelations() {

        HSSFSheet sheet = wb.getSheetAt(1);
        HSSFRow row;
        HSSFCell cell;
        Iterator<Row> rowIterator = sheet.iterator();
        boolean firstLine = true;
        relations = new ArrayList<>();

        System.out.println("Starting relation parsing, filling list relations....");


        while(rowIterator.hasNext()){
            Row ligne = rowIterator.next();

            //On ne traite pas la premiere ligne --> en tete
            if(!firstLine){

                Relation relation = Relation.parseWithExcelRow(ligne);
                relations.add(relation);

            }

            firstLine = false;

        }

        System.out.println("Relation parsing ending. " + relations.size() + " relations");

    }

    public static void main(String[] args) {
        ExcelToEntityParser.getInstance().startParse();
    }
}
