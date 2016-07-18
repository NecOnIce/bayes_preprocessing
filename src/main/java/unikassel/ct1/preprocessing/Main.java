package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.*;
import java.util.stream.Collectors;

/**
 * @author Jonas Scherbaum
 */
public class Main {

		//mallo
		static FastVector      atts1;
        static FastVector atts2;
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







        String arffOutput;

        //generate the .arff-File about the dataSet
        arffOutput = generateArffFile(dataSet);
        //generateArffFile(dataSet);


        //Write to an file

        try(  PrintWriter out = new PrintWriter( "./output.arff" )  ){
            //out.println( data );
            out.println( arffOutput );
        }



    }

    /**
     * generate the .arff-File and save it under the first section in the project (the same section as src)

     * @param dataSet, that have all the data
     * @return data Instance as String
     */
    private static String generateArffFile(DataSet dataSet){
        //----configure the Attributes
        //generate an Vector

        //vector for everything else
        atts1 = new FastVector();

        //vector for the label-data
        atts2 = new FastVector();

        //possible values for the label-attribute of the data
        String[] valuesForLabel = {"walking","standing","sit_down_chair_1","sitting_chair_1","stand_up_chair_1"};

        //for each attribute-header in data --> add these to the attribute vector
        for(String s : dataSet.getHeaders()){

            //We want to classify on label. The class-attribute in weka is a nominal-type-attribute
            if(s.equals("label")){
                //atts1.addElement(new Attribute(s,(FastVector)null));

                //for nominals: we write every possible data in an vector and then these to the ats1-vector

                for(String st : valuesForLabel){
                    atts2.addElement(st);
                }
                atts1.addElement(new Attribute(s,atts2));

            }
            else{
                atts1.addElement(new Attribute(s));
            }

        }

        //======================end of configure the Attributes

        //----create instance for every sample
        data = new Instances("testRelation",atts1,0);

        //======================end create instances


        //----fill with values
        //add values for each sample in the dataset

        for(Sample sa : dataSet){

            //generate for every Sample an seperate value
            vals = new double[data.numAttributes()];

            for(int j =0; j < dataSet.getHeaders().size(); j++){

                //Check Type of Attribute

                //Timestamp
                if(sa.getAttribute(j) instanceof unikassel.ct1.preprocessing.Attribute.TimestampAttribute){
                    vals[j] = (Double) sa.getAttribute(j).getValue();
                }
                //Label
                else if(sa.getAttribute(j) instanceof unikassel.ct1.preprocessing.Attribute.LabelAttribute){
                    //vals[j] = data.attribute(j).addStringValue( sa.getAttribute(j).getValue().toString() );
                    vals[j] = atts2.indexOf(sa.getAttribute(j).getValue());
                }
                //Double
                else if(sa.getAttribute(j) instanceof unikassel.ct1.preprocessing.Attribute.DoubleAttribute){
                    vals[j] = (Double) sa.getAttribute(j).getValue();
                }
            }

            /*
            //first Attribute is a TimeStamp --> a numeric value
            vals[0] = (Double) sa.getAttribute(0).getValue();

            //second Attribute is a label -->a string value
            vals[1] = data.attribute(1).addStringValue( sa.getAttribute(1).getValue().toString() );

            //foreach sensor data: write to the vals-array
            for(int j=2; j < dataSet.getHeaders().size(); j++){
                vals[j] = (Double) sa.getAttribute(j).getValue();
            }
            */

            //write the values to data instance
            data.add(new Instance(1.0,vals));
        }
        return String.valueOf(data);


        /*
        //=======================NEW: build an bayes-model

        //create an empty trainingSet, which have the structure like the .arff-File
        Instances trainningSet = new Instances("TestTrainninSet",atts1,50);

        //define, which attribute is the class-attribute --> the label-attribute
        trainningSet.setClassIndex(1);

        //create instances for the trainningSet (for Test: only one)
        Instance iExample = new
        */

    }

}
