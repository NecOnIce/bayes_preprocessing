package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a whole DataSet. Read from an CSV File.
 *
 * @author Jonas Scherbaum
 */
public class DataSet implements Iterable<Sample> {

    private static final Logger LOG = LoggerFactory.getLogger(DataSet.class);

    private List<Sample> samples = new ArrayList<>();
    private List<String> headers;
    private String timestampHeader;
    private String labelHeader;

    /**
     * Constructs a new DataSet
     *
     * @param labelHeader The name of the Label Header if one exists
     * @param timestampHeader the name of the Timestamp header is one exists
     * @param headers the headers to use for this DataSet
     */
    public DataSet(String labelHeader, String timestampHeader, String[] headers) {
        this.labelHeader = labelHeader;
        this.timestampHeader = timestampHeader;
        this.headers = Arrays.asList(headers);
    }

    /**
     * add an Array of String values representing a new sample.
     *
     * @param values the values to add as new sample
     */
    public void addValues(String[] values) {

        if (values.length != this.headers.size()) {
            String msg = "The values and headers size mismatch! Values: "
                    + values.length + " Headers: " + this.headers.size();
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // create a sample to store the values in
        Sample sample = new Sample(this.samples.size(), this);

        // process each values and corresponding header and add them as attribute
        IntStream.range(0, this.headers.size())
                .forEach((int index) -> {

                    String header = this.headers.get(index);
                    String value = values[index];

                    Attribute<?> attribute = null;
                    if (header.equals(timestampHeader)) {
                        attribute = new Attribute.TimestampAttribute(index, value);
                    } else if (header.equals(labelHeader)) {
                        attribute = new Attribute.LabelAttribute(index, value);
                    } else {
                        attribute = new Attribute.DoubleAttribute(index, value);
                    }

                    sample.addAttribute(index, attribute);
                });

        // add the sample to this DataSet
        this.samples.add(this.samples.size(), sample);
        LOG.debug("Sample successfully added to the DataSet");
    }

    public List<String> getHeaders() {
        return headers;
    }

    public int getSampleCount() {
        return this.samples.size();
    }

    public void addSample(int index, Sample sample) {
        this.samples.add(index, sample);
    }

    @Override
    public Iterator<Sample> iterator() {
        return this.samples.iterator();
    }

    public Stream<Sample> stream() {
        return samples.stream();
    }

    public Stream<Sample> parallelStream() {
        return samples.parallelStream();
    }

    /**
     * get the id of the specified header.
     * Note: use this function rarely, because of performance issues
     *
     * @param header The name of the Header
     * @return The id of the header
     */
    public int getHeaderID(String header) {
        return this.headers.indexOf(header);
    }

    public String getTimestampHeader() {
        return timestampHeader;
    }

    public String getLabelHeader() {
        return labelHeader;
    }
}
