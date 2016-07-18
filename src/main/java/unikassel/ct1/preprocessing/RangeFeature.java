package unikassel.ct1.preprocessing;

import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public class RangeFeature extends Feature<Double> {

    @Override
    public Double calculateFeature(List<Attribute<Double>> attributes) {

        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;

        for (Attribute<Double> attribute: attributes) {
            Double val = attribute.getValue();
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }

        Double range = max - min;
        return range;
    }
}
