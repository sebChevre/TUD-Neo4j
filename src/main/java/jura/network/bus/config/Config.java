package jura.network.bus.config;

/**
 * Created by sce on 04.08.14.
 */
public enum Config {

    EMBED_NEO4J_DB_PATH("db/data"),
    EXCEL_DATA_FILE("data/excel/datas.xls"),
    EXCEL_FAKE_FOR_TEST("data/excel/fake.xls"),
    STATIONS_LOCATIONS_SHEET("0"),
    RELATIONS_SHEET("1");


    public String value;


    Config(String value){
        this.value = value;
    }

    public int intValue(){
        return Integer.parseInt(value);
    }



}
