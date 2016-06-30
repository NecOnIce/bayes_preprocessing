package unikassel.ct1.preprocessing;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * A Sample is one row of the DataSet and consists of the Attributes of the DateSet with one value for each Attribute.
 *
 * @author Jonas Scherbaum
 */
public class Sample implements Iterable<Attribute> {

    private List<Attribute> attributes = new ArrayList<>();
    private int id;
    private DataSet dataSet;

    /**
     * Constructs a new Sample
     *
     * @param id the ID of the sample inside the DataSet
     * @param dataSet the DataSet to which this sample belongs to
     */
    public Sample(int id, DataSet dataSet) {
        this.id = id;
        this.dataSet = dataSet;
    }

    /**
     * adds new Attribute to this Sample.
     * Note that the order of the calls to this method must match the order of the headers inside the DataSet !
     * Adding a new Attribute with an index smaller than the size of the attributes already set will result in a
     * shifting of the already added attributes.
     *
     * TODO improve to protect against invalid attribute adding
     *
     * @param index the index of the Attribute
     * @param attribute The Attribute to add to this Sample
     */
    public void addAttribute(int index, Attribute attribute) {
        attribute.setSample(this);
        this.attributes.add(index, attribute);
    }

    @Override
    public Iterator<Attribute> iterator() {
        return this.attributes.iterator();
    }

    public Stream<Attribute> stream() {
        return attributes.stream();
    }

    public Stream<Attribute> parallelStream() {
        return attributes.parallelStream();
    }

    public int getId() {
        return id;
    }

    public Attribute getAttribute(int index) {
        return this.attributes.get(index);
    }
}
