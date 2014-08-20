package data.process;

import jura.network.bus.config.Config;
import jura.network.bus.entity.geomapping.LocationData;
import jura.network.bus.entity.geomapping.ResultsWrapper;
import jura.network.bus.entity.neo4j.Station;
import jura.network.bus.geomapping.GoogleMapService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import retrofit.RestAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by sce on 14.08.14.
 */
public class CoordinatesFinder {


    RestAdapter adapter;
    GoogleMapService service;


    public CoordinatesFinder(){
        adapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        service = adapter.create(GoogleMapService.class);
    }

    public static void main(String[] args) {

        CoordinatesFinder api = new CoordinatesFinder();
        List<Station> stations = api.readExcelFile();
        api.fillLocalitiesWithGeoInfo(stations);
        api.writeGeoInformationToFile(stations);

        System.out.println(stations.size());





    }


    public void writeGeoInformationToFile(List<Station> stations){
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


                    Station station = Station.parse(cellule.getStringCellValue());

                    Station stationOk = getStationFromList(station, stations);

                    if(null!=stationOk.getLongitude()&&null!=stationOk.getLatitude()){
                        cellLatitude.setCellValue(stationOk.getLatitude());
                        celluleLongitude.setCellValue(stationOk.getLongitude());
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

    Station getStationFromList(Station stat, List<Station> stations){

        for(Station st:stations){
            if(st.equals(stat)){
                return st;
            }
        }
        return null;
    }

    public void fillLocalitiesWithGeoInfo(List<Station> stations){

        for(Station station:stations){

            LocationData data = service.locationData(station.getNom() +",","AIzaSyCkmCWj6zAz5FbbLLWoQvfxaAdXVmWCodU");

            if(data!=null && data.results != null && data.results.size()>0){
                ResultsWrapper w = (ResultsWrapper)getResultWrapperBusStation(data);//.results.get(0);
                if(w!=null){
                    station.setLatitude(w.geometry.location.lat);
                    station.setLongitude(w.geometry.location.lng);
                }

            }

        }

    }


    ResultsWrapper getResultWrapperBusStation(LocationData data){

        for(ResultsWrapper results:data.results){

            for(String geoType:results.types){
                if(geoType.equals("bus_station")){
                    return results;
                }
            }
        }
        return null;
    }

    public List<Station> readExcelFile(){
        POIFSFileSystem fs;
        HSSFWorkbook wb;
        List<Station> stations = null;

        try {
            fs = new POIFSFileSystem(new FileInputStream(Config.EXCEL_DATA_FILE.value));
            wb = new HSSFWorkbook(fs);

            HSSFSheet sheet = wb.getSheetAt(2);
            HSSFRow row;
            HSSFCell cell;
            Iterator<Row> rowIterator = sheet.iterator();

            int rowCount = 0;

            stations = new ArrayList<>();

            while(rowIterator.hasNext()){

                Row ligne = rowIterator.next();

                if(rowCount>0){

                    String stationName = ligne.getCell(0).getStringCellValue();

                    stations.add(Station.parse(stationName));
                }

                rowCount++;
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return stations;
    }
}
