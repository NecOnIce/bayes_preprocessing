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
        String timestampHeader = "Timestamp";
        String labelHeader = "label";

        CT1CSVReader reader = new CT1CSVReader(in, timestampHeader, labelHeader);
        DataSet dataSet = reader.read();

        System.out.println();
    }
}
