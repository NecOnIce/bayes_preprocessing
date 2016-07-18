package unikassel.ct1.preprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public class SlidingWindow<T> {

    private List<Attribute<T>> attributes = new ArrayList<>();
    private Feature<T> feature;
    private int windowSize;
    private float overlapping;

    public SlidingWindow(int windowSize, float overlapping, Feature<T> feature) {
        this.feature = feature;
        this.overlapping = overlapping;
        this.windowSize = windowSize;
    }

    public boolean addAttribute(Attribute<T> attribute) {

        this.attributes.add(attribute);
        return this.attributes.size() == this.windowSize;
    }

    public T calculateFeatureValue() {
        return feature.calculateFeature(this.attributes);
    }

    /**
     * removes the first x Samples from the window.
     *
     * x is the number of Samples which are not part of the overlapping
     * x = windowSize - windowSize*overlapping
     *
     * e.g: 32 - 32*0.25 = 32 - 8 = 24, 8 are part of the overlapping
     */
    public void clearWindow() {

        int numberToRemove = this.windowSize - (int)(this.windowSize*this.overlapping);
        for (int i = 0; i<numberToRemove; i++) {
            this.attributes.remove(0);
        }
    }
}
