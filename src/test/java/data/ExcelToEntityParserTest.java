package data;


import junit.framework.Assert;
import jura.network.bus.config.Config;
import org.junit.Test;

/**
 * Created by sce on 19.08.14.
 */
public class ExcelToEntityParserTest {

    @Test
    public void standardTestWithFileExistWithNonEmptyList(){
        ExcelToEntityParser parser = ExcelToEntityParser.getInstance().startParse();
        parser.localites();
        parser.relations();
        parser.stations();

        Assert.assertTrue(parser.localites().size()>0);
        Assert.assertTrue(parser.stations().size()>0);
        Assert.assertTrue(parser.relations().size()>0);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testAlternateConfigFile(){
        ExcelToEntityParser parser = ExcelToEntityParser.getInstance().configureXlsFile(Config.EXCEL_FAKE_FOR_TEST);
        parser.localites();
        parser.relations();
        parser.stations();
        Assert.fail();
    }

}
