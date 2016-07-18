package unikassel.ct1.preprocessing;

import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public class TimestampFeature extends Feature<Double> {

    @Override
    public Double calculateFeature(List<Attribute<Double>> attributes) {
        return attributes.get(attributes.size()-1).getValue();
    }
}
