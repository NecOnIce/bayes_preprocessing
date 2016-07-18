package unikassel.ct1.preprocessing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonas Scherbaum
 */
public abstract class Feature<T> {

    public abstract T calculateFeature(List<Attribute<T>> attributes);
}
