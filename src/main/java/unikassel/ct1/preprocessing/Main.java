package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Collectors;

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

        // determine frequency
        float frequency = 0;
        double firstValue = 0;
        double filterParam = 50.0/18.0;
        int timestampID = dataSet.getHeaderID(timestampHeader);
        for (Sample sample : dataSet) {

            Attribute.TimestampAttribute timestamp = (Attribute.TimestampAttribute) sample.getAttribute(timestampID);

            int test = (int) (sample.getId() % filterParam);
            LOG.debug(""+test);
            if (test == 0) {
                continue;
            }
            if (firstValue == 0) {
                firstValue = timestamp.getValue();
            } else if ((timestamp.getValue() - firstValue) > 5) {
                break;
            }
            frequency++;
        }

        frequency /= 5;

        LOG.info("The freuqency is: " + frequency);
    }
}
