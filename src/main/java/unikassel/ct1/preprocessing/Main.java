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

/**
 * @author Jonas Scherbaum
 */
public class Main {

		//Franck
		static FastVector      atts1;
		static Instances       data;
		static double []        vals;


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


        //Franck

      //##########################################################
      //       Verbesserte Variante
      //##########################################################

      // 1. set up attributes
      atts1 = new FastVector();

      // - attributes space
      for (String s: dataSet.getHeaders()) {
    	  if(s.equals("label"))
    		  atts1.addElement(new Attribute(s, (FastVector) null));
    	  else
    		  atts1.addElement(new Attribute(s));
      }

      //2. create Instances object
      data = new Instances("MyRelation", atts1, 0);

      //3. fill Data
       for (Sample sam : dataSet){

        	vals = new double[data.numAttributes()];

        	for(int j = 0; j <= data.numAttributes()-1; j++){
        		if (sam.getAttribute(j) instanceof unikassel.ct1.preprocessing.Attribute.LabelAttribute)
        			{
        				String st = sam.getAttribute(j).getValue().toString();
        				vals[j] = data.attribute(j).addStringValue(st);
        			}
        		else
        			vals[j] = (Double) sam.getAttribute(j).getValue();
        	}
        	//write to data instance
            data.add(new Instance(1.0,vals));
        }

        // 4. output data
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
    }
}
