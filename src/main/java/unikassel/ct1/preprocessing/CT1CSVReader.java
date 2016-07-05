package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * CSV Reader implementation for the Comtec CSV Format
 *
 * @author Jonas Scherbaum
 */
public class CT1CSVReader {

    private static final Logger LOG = LoggerFactory.getLogger(CT1CSVReader.class);

    private InputStream in;
    private String timestampHeader;
    private String labelHeader;

    /**
     * Constructs a new Reader
     *
     * @param in the InputStream representing the CSV File to read
     * @param timestampHeader The Header name for the timestamp header if one exist
     * @param labelHeader The Header name for the label header if one exist
     */
    public CT1CSVReader(InputStream in, String timestampHeader, String labelHeader) {
        this.in = in;
        this.timestampHeader = timestampHeader;
        this.labelHeader = labelHeader;
    }

    /**
     * reads in the DataSet from the CSV File.
     *
     * @return The DataSet read from the CSV File
     */
    public DataSet read() {

        Scanner scanner = new Scanner(this.in);
        DataSet dataSet = null;

        // read line wise
        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();

            // skip if line empty
            if (line == null || line.equals("")) {

                //LOG.debug("Empty line skipped.");
                continue;
            }

            // header reached, so read in the header
            if (line.startsWith("#->")) {

                line = line.replaceFirst("#->", "");
                String[] headers = line.split(",");
                dataSet = new DataSet(this.labelHeader, this.timestampHeader, headers);
                //LOG.debug("Header read.");
                continue;
            }

            // first skipp the leading comments
            if (line.startsWith("#")) {

                //LOG.debug("Comment skipped.");
                continue;
            }

            // we have read the header, so we can assume that all following lines are csv formatted
            if (dataSet != null) {

                String[] values = line.split(",");
                dataSet.addValues(values);
                //LOG.debug("Raw of Values read.");
            } else {
                //LOG.warn("Read values, but got no headers before. This will result in missing values inside the DataSet.");
            }
        }

        LOG.info("CSV successfully read.");
        return dataSet;
    }
}
