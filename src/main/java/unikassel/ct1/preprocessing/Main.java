package unikassel.ct1.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

    public static void main(String[] args) throws Exception {
        //For the first: Build datasets about the .csv-data
        //Beginn======================================================================================================

        //Different Persons
        String p1 = "p-3_2015-01-07_10-07-02_0_formatted.csv";
        String p2 = "p-4_2015-01-08_18-25-58_0_formatted.csv";
        String p3 = "p-5_2015-01-08_10-18-57_0_formatted.csv";

        //Here, yo can set the data of the individual persons
        InputStream in = new FileInputStream("./src/main/resources/source/"+p1);
        String timestampHeader = "Timestamp";
        String labelHeader = "label";

        CT1CSVReader reader = new CT1CSVReader(in, timestampHeader, labelHeader);
        DataSet dataSet = reader.read();



        //Configure the Preprocessor:
        //first parameter: the used dataset, on which the feature are calculated
        //second parameter: Overlapping of sliding window
        //third parameter: Window size of sliding window

        PreProcessor preProcessor = new PreProcessor(dataSet, 0.5f, 2.0f);
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
        //End========================================================================================================


        //Now:
        //In Method generateArffFile() we are convert the dataset to Weka-format. For building the .arff-files we are
        //edit the return parameter to String and return the String-value of the Instances. After that we write the return-value
        //in a String and write these to a File.
        //When we write all .arff-files, we can use only the Instances of the Method. For these, we return only the
        //Instances and writes the result to Instance-variable. With these data, we can call the Methods to build the Models.

        //Begin======================================================================================================
        String arffOutput;

        //generate the .arff-File about the dataSet

        //arffOutput = generateArffFile(dataSet);
        //arffOutput = generateArffFile(featureDataSet);
        //generateArffFile(featureDataSet);

        //Write to an file
        /*
        try(  PrintWriter out = new PrintWriter( "./Arff-Data/Preprocessed_WS2_50%OL_P3.arff" )  ){
            //out.println( data );
            out.println( arffOutput );
        }
        */

        //Building the Models

        //Original data, befor Preprocessing
        Instances dataForModels=generateArffFile(dataSet,true);

        //Preprocessed data, after the Preprocessing
        //Instances dataForModels=generateArffFile(featureDataSet,false);

        //buildAnNaiveBayesModelAndEvaluate(dataForModels);

        /**
         * buildAnBayesNetAndEvaluate()
         * First parameter: data for the model.
         * Second parameter: individual options for the Bayes-Net
         *      1: Not initial as Bayes Network, local K2-Algorithm, MDL as score function and simple estimator
         *      2: Not initial as Bayes Network, local Hill-Climbing-Algorithm, MDL as score function and simple estimator
         */
        buildAnBayesNetAndEvaluate(dataForModels,2);



        //End========================================================================================================
    }

    /**
     * Convert the dataset-format in the WEKA-Format. For the first, we use this to write the arff-files, then, we are
     * use the instances-format for building the models
     * @param dataSet - the data to convert
     * @param isOriginalData - if true, the data is NOT preprocessed, if false, the data is preprocessed (important for
     *                       the used Filter, at the End of the Method)
     * @return Instances - the converted data, in the WEKA-format for the models
     * @throws Exception
     */

    private static Instances generateArffFile(DataSet dataSet, Boolean isOriginalData) throws Exception {
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

            //We set the class-attribute: we classify as label. It is the second one
            data.setClassIndex(1);
        }

        //Use a Filter
        //With the second parameter we define, is the actual data original or preprocessed data
        //true: is original-data
        //false: is preprocessed-data

        Instances newData = removeAllAttrOnlyNotAccAndGyro(data,isOriginalData);

        //return String.valueOf(newData);
        return newData;


        //buildAnNaiveBayesModelAndEvaluate(newData);
        //buildAnBayesNetAndEvaluate(newData);

    }

    /**
     * Remove the timestamp as attribute
     * @param data - the instances on base of the original data, with timestamp as attribute
     * @return newData - the instances without a timestamp
     * @throws Exception
     */
    private static Instances removeTimeStampAsAttribute(Instances data) throws Exception {
        Remove remove = new Remove();

        remove.setAttributeIndices("first");
        remove.setInputFormat(data);

        Instances newData = Filter.useFilter(data,remove);

        return newData;
    }

    /**
     * Remove all attributes except accelerometer, gyroscope and label
     * @param data - Instances with ALL ATTRIBUTES
     * @return newData - Instances, only with accelerometer, gyroscope and label as attributes
     * @throws Exception
     */
    private static Instances removeAllAttrOnlyNotAccAndGyro(Instances data, Boolean isOriginalData) throws Exception {

        //We are removing the timestamp of the data
        Instances dataWithoutTimeStamp = removeTimeStampAsAttribute(data);

        Remove remove = new Remove();


        //After the Preprocessing (isOriginalData == false) we have more Attributes, like Acc_x_Mean, Acc_x_Variance,...
        //In these case, we must remove more Attributes, because we want only Accelerometer and Gyroscope
        if(isOriginalData){
            System.out.println("Original-data");
            remove.setAttributeIndices("first-7");
            remove.setInvertSelection(true);
        }
        else {
            System.out.println("Preprocessed-data");
            remove.setAttributeIndices("first-19");
            remove.setInvertSelection(true);
        }

        remove.setInputFormat(dataWithoutTimeStamp);

        Instances newData = Filter.useFilter(dataWithoutTimeStamp,remove);

        return newData;
    }

    /**
     * Build an naive Bayes Model and evaluate with 10-fold cross validation
     * @param data - the data to build the model
     */
    private static void buildAnNaiveBayesModelAndEvaluate(Instances data){
        //build an naiv bayes model
        NaiveBayes nb = new NaiveBayes();

        try {
            //nb.setOptions(options);

            nb.buildClassifier(data);
            System.out.println("Using Naive Bayes");


            //make a 10 fold cross validation
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(nb,data,10,new Random(1));


            //make the output of the evaluation

            //standard values of evaluation
            System.out.println(eval.toSummaryString(false));

            //confusion matrix
            System.out.println(eval.toMatrixString());



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Build an Bayes Net and evaluate with 10-fold cross validation
     * @param data - the data to build the model
     * @throws Exception
     */
    private static void buildAnBayesNetAndEvaluate(Instances data,int optionsOfBayesNet) throws Exception {
        BayesNet net = new BayesNet();

        //Define the options for the bayes net
        //options_Se_MDL_K2: Not initial as Bayes Network, local K2-Algorithm, MDL as score function and simple estimator
        //Options_Se_MDL_HC: Not initial as Bayes Network, local Hill-Climbing-Algorithm, MDL as score function
        // and simple estimator

        String options_Se_MDL_K2[] = weka.core.Utils.splitOptions("-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -N -S MDL -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5");
        String options_Se_MDL_Hc[] = weka.core.Utils.splitOptions("-D -Q weka.classifiers.bayes.net.search.local.HillClimber -- -N -P 1 -S MDL -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5");

        if(optionsOfBayesNet == 1){
            net.setOptions(options_Se_MDL_K2);
            System.out.println("Using Bayes-Net with K2");
        }
        else if(optionsOfBayesNet == 2){
            net.setOptions(options_Se_MDL_Hc);
            System.out.println("Using Bayes-Net with Hill-Climbing");
        }


        net.buildClassifier(data);

        //make a 10 fold cross validation
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(net,data,10,new Random(1));

        //make the output of the evaluation

        //standard values of evaluation
        System.out.println(eval.toSummaryString(false));

        //confusion matrix
        System.out.println(eval.toMatrixString());



    }
}
