package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

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

    public static void main(String[] args) throws IOException {

        InputStream in = new FileInputStream("./src/main/resources/source/p-5_2015-01-08_10-18-57_0_formatted.csv");
        String timestampHeader = "Timestamp";
        String labelHeader = "label";

        CT1CSVReader reader = new CT1CSVReader(in, timestampHeader, labelHeader);
        DataSet dataSet = reader.read();

        PreProcessor preProcessor = new PreProcessor(dataSet, 0.25f, 1.0f);
        preProcessor.preprocess();
        DataSet featureDataSet = preProcessor.getFeatureDataSet();

        File outputFile = new File("./src/main/resources/features.csv");
        Charset charset = Charset.forName("UTF-8");
        BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath(), charset);

        String columnSeperator = ",";
        List<String> headers = featureDataSet.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            writer.write(header, 0, header.length());
            if (i < (headers.size()-1)) {
                writer.write(columnSeperator, 0, columnSeperator.length());
            }
        }
        writer.newLine();

        Iterator<Sample> itSample = featureDataSet.iterator();
        while (itSample.hasNext()) {

            Sample sample = itSample.next();
            Iterator<unikassel.ct1.preprocessing.Attribute> it = sample.iterator();
            while (it.hasNext()) {
                unikassel.ct1.preprocessing.Attribute attribute = it.next();
                String attributeValue = attribute.convertToString();
                writer.write(attributeValue, 0, attributeValue.length());

                if (it.hasNext()) {
                    writer.write(columnSeperator, 0, columnSeperator.length());
                }
            }
            if (itSample.hasNext()) {
                writer.newLine();
            }
        }

        writer.flush();
        writer.close();


        //Franck determine frequency
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
        */
    }
}
