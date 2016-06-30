package unikassel.ct1.preprocessing;

/**
 * An Attribute represents a Single Value of a Sample of a DataSet.
 * An Attribute combines the value with the type of the value and the name if the Header, as well as the
 * id of the header.
 *
 * @param <T> The Type of the Value for this Attribute
 *
 * @author Jonas Scherbaum
 */
public abstract class Attribute<T> {

    protected int id;
    protected T value;
    protected Sample sample;

    /**
     * Constructs a new Attribute
     * The Value as String will be automatically transformed into T
     *
     * @param id the id of the Attribute (Header relation)
     * @param value the Value of the Attribute as String.
     */
    public Attribute(int id, String value) {
        this.id = id;
        this.value = this.convertValue(value);
    }

    /**
     * sets the Sample to which this Attribute belongs to
     *
     * @param sample The sample
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * Converts the given value as String into the Type T
     *
     * @param valueAsString The value as String
     * @return The value as T
     */
    protected abstract T convertValue(String valueAsString);

    /**
     * converts the value of this Attribute into a String.
     *
     * @return the Value as String
     */
    protected abstract String convertToString();

    public T getValue() {
        return value;
    }

    /**
     * Default Implementation for all values
     */
    public static class DoubleAttribute extends  Attribute<Double> {

        public DoubleAttribute(int id, String value) {
            super(id, value);
        }

        @Override
        protected Double convertValue(String valueAsString) {
            return Double.parseDouble(valueAsString);
        }

        @Override
        protected String convertToString() {
            return String.valueOf(this.value);
        }
    }

    /**
     * Default Implementation for the Timestamp type
     */
    public static class TimestampAttribute extends Attribute<Double> {

        public TimestampAttribute(int id, String value) {
            super(id, value);
        }

        @Override
        protected Double convertValue(String valueAsString) {
            return Double.parseDouble(valueAsString);
        }

        @Override
        protected String convertToString() {
            return String.valueOf(this.value);
        }
    }

    /**
     * Default Implementation for the Label type
     */
    public static class LabelAttribute extends Attribute<String> {

        public LabelAttribute(int id, String value) {
            super(id, value);
        }

        @Override
        protected String convertValue(String valueAsString) {
            return valueAsString;
        }

        @Override
        protected String convertToString() {
            return this.value;
        }
    }
}
