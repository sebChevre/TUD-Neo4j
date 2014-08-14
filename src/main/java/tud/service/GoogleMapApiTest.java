package tud.service;

import data.Config;
import data.entity.Locality;
import data.entity.Relation;
import data.entity.Station;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import retrofit.RestAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by sce on 14.08.14.
 */
public class GoogleMapApiTest {


    RestAdapter adapter;
    GoogleMapService service;


    public GoogleMapApiTest(){
        adapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        service = adapter.create(GoogleMapService.class);
    }

    public static void main(String[] args) {

        System.setProperty("http.proxyHost", "proxy.ju.globaz.ch");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "proxy.ju.globaz.ch");
        System.setProperty("https.proxyPort", "8080");

        GoogleMapApiTest api = new GoogleMapApiTest();
        List<Locality> localities = api.readExcelFile();
        api.fillLocalitiesWithGeoInfo(localities);
        api.writeGeoInformationToFile(localities);

        System.out.println(localities.size());





    }


    public void writeGeoInformationToFile(List<Locality> localities){
        POIFSFileSystem fs;
        HSSFWorkbook wb;
        try {
            fs = new POIFSFileSystem(new FileInputStream(Config.EXCEL_DATA_FILE.value));
            wb = new HSSFWorkbook(fs);

            HSSFSheet sheet = wb.getSheetAt(2);
            HSSFRow row;
            HSSFCell cell;
            Iterator<Row> rowIterator = sheet.iterator();

            int rowCount = 0;

            while(rowIterator.hasNext()){

                Row ligne = rowIterator.next();

                if(rowCount>0){

                    Cell cellule = ligne.getCell(0);
                    Cell cellLatitude = ligne.getCell(1);



                    Cell celluleLongitude = ligne.getCell(2);


                    Locality localite = Locality.parse(cellule.getStringCellValue());

                    Locality locaOk = getLocalityFromList(localite,localities);

                    if(null!=locaOk.getLongitude()&&null!=locaOk.getLatitude()){
                        cellLatitude.setCellValue(locaOk.getLatitude());
                        celluleLongitude.setCellValue(locaOk.getLongitude());
                    }


                }

                rowCount++;
            }

            FileOutputStream outFile =new FileOutputStream(new File(Config.EXCEL_DATA_FILE.value));
            wb.write(outFile);
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Locality getLocalityFromList(Locality loca,List<Locality> localities){

        for(Locality loc:localities){
            if(loc.equals(loca)){
                return loc;
            }
        }
        return null;
    }

    public void fillLocalitiesWithGeoInfo(List<Locality> localities){

        for(Locality locality:localities){

            LocationData data = service.locationData(locality.getNom(),"AIzaSyCkmCWj6zAz5FbbLLWoQvfxaAdXVmWCodU");

            if(data!=null && data.results != null && data.results.size()>0){
                ResultsWrapper w = (ResultsWrapper)data.results.get(0);
                locality.setLatitude(w.geometry.location.lat);
                locality.setLongitude(w.geometry.location.lng);
            }

        }

    }


    public List<Locality> readExcelFile(){
        POIFSFileSystem fs;
        HSSFWorkbook wb;
        List<Locality> localites = null;

        try {
            fs = new POIFSFileSystem(new FileInputStream(Config.EXCEL_DATA_FILE.value));
            wb = new HSSFWorkbook(fs);

            HSSFSheet sheet = wb.getSheetAt(2);
            HSSFRow row;
            HSSFCell cell;
            Iterator<Row> rowIterator = sheet.iterator();

            int rowCount = 0;

            localites = new ArrayList<>();

            while(rowIterator.hasNext()){

                Row ligne = rowIterator.next();

                if(rowCount>0){

                    String localiteName = ligne.getCell(0).getStringCellValue();

                    localites.add(Locality.parse(localiteName));
                }

                rowCount++;
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return localites;
    }
}
