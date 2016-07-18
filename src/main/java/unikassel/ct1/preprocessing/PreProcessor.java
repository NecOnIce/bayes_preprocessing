package unikassel.ct1.preprocessing;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
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
     * e.g. 32hz -> 32
     */
    private float sourceFrequency;

    /**
     * the frequency to use for the pre processing
     * e.g. 32hz -> 32
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

    private Map<Integer, List<SlidingWindow>> attributeToWindowsMap = new HashMap<>();

    private Map<String, SlidingWindow> featureHeaderToWindowMap = new HashMap<>();

    private DataSet featureDataSet;

    /**
     * Constructs and initializes the PreProcessor.
     * This includes:
     * - determining the source frequency
     * - setting the target frequency to source frequency as default
     *
     * @param dataSet the DataSet to pre process
     */
    public PreProcessor(DataSet dataSet, float overlapping, float windowSize) {
        this.dataSet = dataSet;
        determineCurrentFrequency();
        this.targetFrequency = sourceFrequency;
        this.overlapping = overlapping;
        this.windowSize = windowSize;

        // get the headers from the source DataSet and construct the Features DataSet
        List<String> sourceHeaders = this.dataSet.getHeaders();
        List<String> newHeaders = new ArrayList<>();

        int windowElements = (int)(this.windowSize*this.targetFrequency);

        sourceHeaders.forEach((header) -> {

            // timestamp is the leading header
            if (header.equals(dataSet.getTimestampHeader())) {
                newHeaders.add(0, header);
                Feature<Double> timeStampFeature = new TimestampFeature();
                SlidingWindow<Double> slidingWindow =
                        new SlidingWindow<Double>(windowElements, this.overlapping, timeStampFeature);
                attributeToWindowsMap.putIfAbsent(dataSet.getHeaderID(header),
                        Collections.singletonList(slidingWindow));
                featureHeaderToWindowMap.put(header, slidingWindow);
                return;
            }

            // label is the second header
            if (header.equals(dataSet.getLabelHeader())) {
                newHeaders.add(1, header);
                Feature<String> labelFeature = new LabelFeature();
                SlidingWindow<String> slidingWindow =
                        new SlidingWindow<>(windowElements, this.overlapping, labelFeature);
                attributeToWindowsMap.putIfAbsent(dataSet.getHeaderID(header),
                        Collections.singletonList(slidingWindow));
                featureHeaderToWindowMap.put(header, slidingWindow);
                return;
            }

            // for each original header create for each Feature a new header
            String meanHeader = header + "_MEAN";
            String varianceHeader = header + "_VARIANCE";
            String rangeHeader = header + "_RANGE";
            newHeaders.add(meanHeader);
            newHeaders.add(varianceHeader);
            newHeaders.add(rangeHeader);

            Feature<Double> meanFeature = new MeanFeature();
            Feature<Double> varianceFeature = new VarianceFeature();
            Feature<Double> rangeFeature = new RangeFeature();

            List<SlidingWindow> slidingWindows = new ArrayList<>();

            SlidingWindow<Double> meanWindow = new SlidingWindow<>(windowElements, this.overlapping,
                    meanFeature);
            SlidingWindow<Double> varianceWindow = new SlidingWindow<>(windowElements,
                    this.overlapping, varianceFeature);
            SlidingWindow<Double> rangeWindow = new SlidingWindow<>(windowElements, this.overlapping,
                    rangeFeature);

            slidingWindows.add(meanWindow);
            slidingWindows.add(varianceWindow);
            slidingWindows.add(rangeWindow);

            attributeToWindowsMap.putIfAbsent(dataSet.getHeaderID(header), slidingWindows);
            featureHeaderToWindowMap.put(meanHeader, meanWindow);
            featureHeaderToWindowMap.put(varianceHeader, varianceWindow);
            featureHeaderToWindowMap.put(rangeHeader, rangeWindow);
        });

        this.featureDataSet = new DataSet(dataSet.getLabelHeader(), dataSet.getTimestampHeader(),
                newHeaders.toArray(new String[0]));
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

            boolean windowsFull = true;
            // filter out samples according to the target frequency
            if (filterFrequency(sample)) {
                continue;
            }

            // filling the sliding windows
            for (Attribute att : sample) {

                List<SlidingWindow> windows = attributeToWindowsMap.get(att.id);
                for (SlidingWindow window : windows) {
                    windowsFull &= window.addAttribute(att);
                }
            }

            if (windowsFull) {

                int sampleIndex = this.featureDataSet.getSampleCount();
                Sample featureSample = new Sample(sampleIndex, this.featureDataSet);
                List<String> headers = this.featureDataSet.getHeaders();
                for (int i = 0; i < headers.size(); i++) {
                    String header = headers.get(i);
                    SlidingWindow window = this.featureHeaderToWindowMap.get(header);
                    Object featureValue = window.calculateFeatureValue();

                    if (header.equals(this.featureDataSet.getTimestampHeader())) {
                        Attribute timestampAttribute = new Attribute.TimestampAttribute(i,
                                String.valueOf(featureValue));
                        featureSample.addAttribute(i, timestampAttribute);
                    } else if (header.equals(this.featureDataSet.getLabelHeader())) {
                        Attribute labelAttribute = new Attribute.LabelAttribute(i, String.valueOf(featureValue));
                        featureSample.addAttribute(i, labelAttribute);
                    } else {
                        Attribute attribute = new Attribute.DoubleAttribute(i, String.valueOf(featureValue));
                        featureSample.addAttribute(i, attribute);
                    }

                    window.clearWindow();
                }

                this.featureDataSet.addSample(sampleIndex, featureSample);

            }
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

    public DataSet getFeatureDataSet() {
        return featureDataSet;
    }
}
