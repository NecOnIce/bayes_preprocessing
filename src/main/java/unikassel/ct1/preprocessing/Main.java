package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws FileNotFoundException {

        InputStream in = new FileInputStream("./src/main/resources/p-1_2015-01-06_12-03-39_0_formatted.csv");

        CT1CSVReader reader = new CT1CSVReader(in);
        CT1CSVReader.CSV csv = reader.read();

        List<String> headers = csv.getHeaders();
        LOG.info("Headers: ");
        String hString = "";
        for (String s : headers) {
            hString += s + ",";
        }
        LOG.info(hString);

        List<List<Double>> values = csv.getValues();
        LOG.info("Values:");
        for (List<Double> val : values) {

            String v = "";
            for (Double d : val) {
                v += d.toString() + ",";
            }
            LOG.info(v);
        }
    }
}
