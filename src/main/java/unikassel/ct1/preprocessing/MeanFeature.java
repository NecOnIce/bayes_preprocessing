package unikassel.ct1.preprocessing;

import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public class MeanFeature extends Feature<Double> {

    @Override
    public Double calculateFeature(List<Attribute<Double>> attributes) {

        Double sum = 0.0;
        for (Attribute<Double> attribute : attributes) {
            sum += attribute.getValue();
        }

        Double mean = sum / attributes.size();
        return mean;
    }
}
