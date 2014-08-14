package data;

/**
 * Created by sce on 04.08.14.
 */
public enum Config {

    EMBED_GRAPH_DB_PATH("db/data"),
    EXCEL_DATA_FILE("data/excel/datas.xls");


    public String value;

    Config(String value){
        this.value = value;
    }
}
