package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

/**
 * @author Jonas Scherbaum
 */
public class Main {

		//mallo
		static FastVector      atts1;
		static Instances       data;
		static double[]        vals;


    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws FileNotFoundException {

        InputStream in = new FileInputStream("./src/main/resources/p-1_2015-01-06_12-03-39_0_formatted.csv");
        String timestampHeader = "Timestamp";
        String labelHeader = "label";

        CT1CSVReader reader = new CT1CSVReader(in, timestampHeader, labelHeader);
        DataSet dataSet = reader.read();

        // determine frequency
        float frequency = 0;
        double firstValue = 0;
        double filterParam = 50.0/18.0;
        int timestampID = dataSet.getHeaderID(timestampHeader);
        for (Sample sample : dataSet) {

        	unikassel.ct1.preprocessing.Attribute.TimestampAttribute timestamp = (unikassel.ct1.preprocessing.Attribute.TimestampAttribute) sample.getAttribute(timestampID);

            int test = (int) (sample.getId() % filterParam);
            LOG.debug(""+test);
            if (test == 0) {
                continue;
            }
            if (firstValue == 0) {
                firstValue = timestamp.getValue();
            } else if ((timestamp.getValue() - firstValue) > 5) {
                break;
            }
            frequency++;
        }

        frequency /= 5;

        LOG.info("The freuqency is: " + frequency);


        //Franck determine frequency

        atts1 = new FastVector();
        String str = "";

        for (Sample sample : dataSet) {

        	for(unikassel.ct1.preprocessing.Attribute attr : sample){
        		if (attr instanceof unikassel.ct1.preprocessing.Attribute.DoubleAttribute){

        			str = attr.getValue().toString();

        			str = attr.convertToString();
        			System.out.println("-----------DoubleAttribute---------------");
            		System.out.println(str);
            		atts1.addElement(new Attribute(str));
        		}
        		if (attr instanceof unikassel.ct1.preprocessing.Attribute.LabelAttribute){
        			//LabelAttribute
        			str = attr.convertToString();
        			System.out.println("-----------LabelAttribute---------------");
            		System.out.println(str);
            		atts1.addElement(new Attribute(str, (FastVector) null));
        		}
        		if (attr instanceof unikassel.ct1.preprocessing.Attribute.TimestampAttribute){
        			//TimestampAttribute
        			str = attr.convertToString();
        			System.out.println("-----------TimestampAttribute---------------");
            		System.out.println(str);
            		atts1.addElement(new Attribute(str));
        		}

        	}
        }


        data.add(new Instance(1.0, vals));

        //System.out.println(data);

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        try {
           saver.setFile(new File("./output.arff"));
           saver.writeBatch();
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
        //########################################################################
    }
}
