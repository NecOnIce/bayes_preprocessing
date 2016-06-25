package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author Jonas Scherbaum
 */
public class CT1CSVReader {

    private static final Logger LOG = LoggerFactory.getLogger(CT1CSVReader.class);

    private InputStream in;

    public CT1CSVReader(InputStream in) {
        this.in = in;
    }

    public CSV read() {

        Scanner scanner = new Scanner(this.in);

        List<String> headerList = null;
        List<List<Double>> valuesList = new ArrayList<>();

        boolean headerRead = false;
        // read line wise
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // skip if line empty
            if (line == null || line.equals("")) {
                LOG.debug("Empty line skipped.");
                continue;
            }

            // header reached, so read in the header
            if (line.startsWith("#->")) {
                headerRead = true;
                line = line.replaceFirst(">", "");
                String[] headers = line.split(",");
                headerList = Arrays.asList(headers);
                LOG.debug("Header read.");
                continue;
            }

            // first skipp the leading comments
            if (line.startsWith("#")) {
                LOG.debug("Comment skipped.");
                continue;
            }

            // we have read the header, so we can assume that all following lines are csv formatted
            if (headerRead) {
                String[] values = line.split(",");
                List<Double> dList = Arrays.asList(values)
                        .stream()
                        .map(Double::valueOf)
                        .collect(Collectors.toList());
                valuesList.add(dList);
                LOG.debug("Raw of Values read.");
            }
        }

        LOG.info("CSV successfully read.");
        return new CSV(headerList, valuesList);
    }

    public static class CSV {

        private List<String> headers;
        private List<List<Double>> values;

        public CSV(List<String> headers, List<List<Double>> values) {
            this.headers = headers;
            this.values = values;
        }

        public List<String> getHeaders() {
            return this.headers;
        }

        public List<List<Double>> getValues() {
            return values;
        }
    }
}
