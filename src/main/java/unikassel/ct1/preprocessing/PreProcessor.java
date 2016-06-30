package unikassel.ct1.preprocessing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author Jonas Scherbaum
 */
public class PreProcessor {

    /**
     * The DataSet to pre process
     */
    private DataSet dataSet;

    /**
     * the freuqency of the DataSet to pre process
     */
    private float sourceFrequency;

    /**
     * the frequency to use for the pre processing
     */
    private float targetFrequency;

    /**
     * the size of the window in seconds
     * e.g.: 0.5s, 1s, 2s, etc...
     */
    private float windowSize;

    /**
     * the amount of the overlapping inside the sliding window.
     * this must be between 0 and 1 -> 0% to 100%
     */
    private float overlapping;

    /**
     * the id of the timestamp header
     */
    private int timestampID;

    /**
     * The Filter Parameter used to filter out the id's
     */
    private double frequencyFilterParam;

    /**
     * Constructs and initializes the PreProcessor.
     * This includes:
     * - determining the source frequency
     * - setting the target frequency to source frequency as default
     *
     * @param dataSet the DataSet to pre process
     */
    public PreProcessor(DataSet dataSet) {
        this.dataSet = dataSet;
        determineCurrentFrequency();
        this.targetFrequency = sourceFrequency;
    }

    /**
     * determines the source frequency of the DataSet.
     * It will be used for the filtering of the DataSet with the FrequencyFilter
     */
    private void determineCurrentFrequency() {

        // determine frequency
        float frequency = 0;
        double firstValue = 0;
        int timestampID = this.dataSet.getHeaderID(this.dataSet.getTimestampHeader());
        for (Sample sample : this.dataSet) {

            Attribute.TimestampAttribute timestamp = (Attribute.TimestampAttribute) sample.getAttribute(timestampID);
            if (firstValue == 0) {
                firstValue = timestamp.getValue();
            } else if ((timestamp.getValue() - firstValue) > 5) {
                break;
            }
            frequency++;
        }

        this.sourceFrequency = frequency/  5;
    }

    /**
     * starts the preprocessing of the DataSet and generates a new DataSet containing the calculated Features
     */
    public void preprocess() {

        // first initialize the preprocessing
        initializePreprocessing();

        // begin the preprocessing by iterating over all samples
        for (Sample sample : this.dataSet) {

            // filter out samples according to the target frequency
            if (filterFrequency(sample)) {
                continue;
            }

            // filling the sliding window

            
        }

    }

    /**
     * creates the target DataSet and constructs the new headers for the calculated features.
     */
    private void initializePreprocessing() {

        this.timestampID = dataSet.getHeaderID(dataSet.getTimestampHeader());
        this.frequencyFilterParam = this.sourceFrequency/(this.sourceFrequency-this.targetFrequency);
    }

    private boolean filterFrequency(Sample sample) {

        int test = (int) (sample.getId() % this.frequencyFilterParam);
        return test == 0;
    }

    public float getSourceFrequency() {
        return sourceFrequency;
    }

    public float getTargetFrequency() {
        return targetFrequency;
    }

    public void setTargetFrequency(float targetFrequency) {
        this.targetFrequency = targetFrequency;
    }

    public float getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(float windowSize) {
        this.windowSize = windowSize;
    }

    public float getOverlapping() {
        return overlapping;
    }

    public void setOverlapping(float overlapping) {
        this.overlapping = overlapping;
    }
}
