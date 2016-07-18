package unikassel.ct1.preprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jonas Scherbaum
 */
public class LabelFeature extends Feature<String> {

    @Override
    public String calculateFeature(List<Attribute<String>> attributes) {
        Map<String, Integer> countMap = new HashMap<>();

        for (Attribute<String> la : attributes) {

            Integer count = countMap.get(la.getValue());
            if (count == null) {
                count = 0;
            }
            count++;
            countMap.put(la.getValue(), count);
        }

        String maxLabel = null;
        int maxCount = 0;
        for (String key : countMap.keySet()) {

            Integer count = countMap.get(key);

            if (count > maxCount) {
                maxLabel = key;
                maxCount = count;
            }
        }
        return maxLabel;
    }
}
