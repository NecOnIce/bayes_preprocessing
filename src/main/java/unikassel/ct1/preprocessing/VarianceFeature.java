package unikassel.ct1.preprocessing;

import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public class VarianceFeature extends MeanFeature {

    @Override
    public Double calculateFeature(List<Attribute<Double>> attributes) {

        Double mean = super.calculateFeature(attributes);

        Double sum = 0.0;
        for (Attribute<Double> attribute : attributes) {
            Double subtraction = attribute.getValue() - mean;
            sum += subtraction*subtraction;
        }

        Double variance = sum / (attributes.size()-1);
        return variance;
    }
}
