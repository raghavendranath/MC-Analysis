package uiowa.hhaim.attractiveness;

/**
 * Use the Longitudinal.java file to get the hydropathy averages of patients
 * Then come to this program for calculating the attractiveness of patients
 * Used the below jar file for kmeans
 * https://github.com/pierredavidbelanger/ekmeans - Very important for documentation
 * Modify the data as shown in file U:\ResearchData\rdss_hhaim\LAB PROJECTS\Raghav\Analysis\Attractiveness\B_Eur_Sequence.xls
 * Use Hydropathy lookup for get the lookup for each sample - For hydropathy scores
 * If not use the same lookup table for size scores of each amino acids
 * Calculate centroid of each timepoint of a patient
 * Combinations generator: http://www.dcode.fr/combinations
 * http://java-ml.sourceforge.net/api/0.1.7/net/sf/javaml/clustering/KMeans.html -> Java machine learning
 * http://www.philippe-fournier-viger.com/spmf/index.php?link=documentation.php
 * Weka jar file and example for kmeans https://stackoverflow.com/questions/25668512/k-means-weka-java-code
 */

import ca.pjer.ekmeans.*;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.Clusterer.*;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.KMeans.*;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Dataset.*;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.data.FileHandler.*;

import java.io.*;
import java.sql.Time;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import be.abeel.io.GZIPPrintWriter;

//imports for spmf
import ca.pfv.spmf.algorithms.clustering.kmeans.*;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


class Patient {
    String ID;
    double resultDelta;
    ArrayList<TimePoint> tp;
    static class TimePoint{
        String ID;
        //SinglePoint centroid defined i.e. K=1 in k means
        double[] centroid;
        ArrayList<Sample> samples;
        TimePoint(String ID, int size){
            this.ID = ID;
            this.centroid = new double[size];
            this.samples = new ArrayList<>();
        }
        public static class Sample{
            double[] values;
            Sample(double[] values, int sizeOfSample){
                this.values = new double[sizeOfSample];
                this.values = values;
            }
        }
    }


    Patient(String ID) {
        this.ID = ID;
        this.tp = new ArrayList<>();
    }
    boolean contains(String dayTP){
        if(tp.isEmpty())
            return false;
        for(TimePoint time:tp ){
            if(dayTP.equals( time.ID ))
                return true;
        }
        return false;
    }
    int getTimePointIndex(String dayTP){
        for(int i=0; i< tp.size(); i++){
            if(dayTP.equals( tp.get(i).ID ))
                return i;
        }
        return -1;
    }
}



public class Attractiveness {
    private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Attractiveness\\Actual Data\\B_LS_2G12.txt";
    private static final String Datafile_popul = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Attractiveness\\forArff\\b_pop_2G12.csv";

    public static void main(String args[]){

        BufferedReader br = null,br1 = null;
        FileReader fr = null;
        try {
            String sCurrentLine;
            fr = new FileReader(Datafile);
            br = new BufferedReader(fr);
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }

            String[] temp = result.get( 0 );
            HashMap<String, Integer> h = new HashMap<>();
            ArrayList<String> positions = new ArrayList<>(  );
            for (int i = 2; i < temp.length; i++) {
                if (!h.containsKey( temp[i] )){
                    h.put( temp[i], i - 2 );
                    positions.add(temp[i]);
                }

            }

            result.remove( 0 );
            ArrayList<Patient> patients = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>();
            for(String[] data: result) {
                if (!previous_patients.contains( data[0] )) {
                    patients.add( new Patient( data[0] ) );
                    previous_patients.add( data[0] );
                }
                int pat_index = previous_patients.indexOf( data[0] );
                if (pat_index > patients.size())
                    break;
                Patient patient_temp = patients.get( pat_index );
                if(!patient_temp.contains( data[1] )) {
                    patient_temp.tp.add( new Patient.TimePoint(data[1],h.size()));
                }
                int tp_index = patient_temp.getTimePointIndex( data[1] );
                if(tp_index == -1)
                    break;
                Patient.TimePoint tp_temp = patient_temp.tp.get(tp_index);
                double val[] = new double[h.size()];
                for(int i=2; i<data.length;i++){
                    val[i-2] = Double.parseDouble( data[i] );
                }
                tp_temp.samples.add(new Patient.TimePoint.Sample( val,h.size() ));
            }

            //Get all population centroids
            //For this first create a .arff file containing the population information and get the centroids
            br1 = new BufferedReader(new FileReader(Datafile_popul));
            ArrayList<String> result1 = new ArrayList<>();
            while ((sCurrentLine = br1.readLine()) != null) {
                result1.add(sCurrentLine.trim());
            }
            result1.remove(0);
            int flag = 1; //to check if I can create a directory for arff files
            //Create a folder beforehand
            boolean success = (new File( "C:\\Users\\kandula.HEALTHCARE\\Desktop\\Combination"+flag )).mkdirs();
            if(success)
                System.out.println("Folder created");
            else{
                System.out.println("Folder not created");
                System.exit(0);
            }



            PrintWriter writer1 = new PrintWriter("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Combination"+flag+"\\pop.arff");
            writer1.append( "@RELATION b-population\n\n" );
            for(int i=0; i<positions.size();i++){
                writer1.append( "@ATTRIBUTE "+positions.get(i)+" REAL\n" );
            }
            writer1.append( "@DATA\n");
            for(String item:result1)
                writer1.append( item+"\n" );
            writer1.close();


            int noOfClusters = 9;
            int countInCluster[] = new int[noOfClusters];
            Instances dataPop = ConverterUtils.DataSource.read("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Combination"+flag+"\\pop.arff");
            SimpleKMeans kMeansPop = new SimpleKMeans();
            kMeansPop.setNumClusters(noOfClusters);
            kMeansPop.buildClusterer(dataPop);

            // print out the cluster centroids
            Instances centroidPop = kMeansPop.getClusterCentroids();
            double[][] popAttracters = new double[noOfClusters][positions.size()];
            for(int i=0; i< noOfClusters;i++){
                popAttracters[i] = centroidPop.get(i).toDoubleArray();
            }
            System.out.println("Population attractors are:");
            for(int i=0;i<popAttracters.length;i++){
                for(int j=0; j<popAttracters[0].length;j++){
                    System.out.print(popAttracters[i][j]+",");
                }
                System.out.println();
            }



            System.out.println("Number of patients in each cluster:");
            countInCluster = kMeansPop.getClusterSizes();
            for(int i=0;i<countInCluster.length;i++)
                System.out.println(countInCluster[i]);


            System.out.println("Test ");





            //Get all combinations of the features as follows:
            //Use http://www.dcode.fr/combinations for different combinations
            //We are interested in 5 feature combinations. So defining a variable to define the no of features needed
            int features_k = 5;
            //different combinations
            int comb = combination(h.size(),features_k);

            //(If you are in IntelliJ)Go to the edit menu and select column selection mode.
            //Press ctrl and left click on mouse point to select multiple lines and add {,} and , to the elements from the http://www.dcode.fr/combinations
            int[][] position_combinations = new int[][]{
                    {295, 332, 339, 386, 448},
                    {295, 332, 339, 392, 448},
                    {295, 332, 386, 392, 448},
                    {295, 339, 386, 392, 448},
                    {295, 332, 339, 386, 392},
                    {332, 339, 386, 392, 448}
            };





            Random random = new Random(System.currentTimeMillis());
            for(Patient patient: patients){
                success = (new File( "C:\\Users\\kandula.HEALTHCARE\\Desktop\\Combination"+flag+"\\"+patient.ID )).mkdirs();
                if(success)
                    System.out.println("Folder created");
                else{
                    System.out.println("Folder not created");
                    System.exit(0);
                }
                for(Patient.TimePoint timePoint: patient.tp){
                    //creating arff file
                    PrintWriter writer = new PrintWriter("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Combination"+flag+"\\"+patient.ID+"\\"+timePoint.ID+".arff");
                    writer.append( "@RELATION "+patient.ID+"\n\n" );
                    for(int i=0; i<positions.size();i++){
                        writer.append( "@ATTRIBUTE "+positions.get(i)+" REAL\n" );
                    }
                    writer.append( "@DATA\n");
                    for(Patient.TimePoint.Sample sample: timePoint.samples){
                        for(int m=0; m<sample.values.length-1 ;m++){
                            writer.append(sample.values[m]+",");
                        }
                        writer.append(sample.values[sample.values.length-1]+"\n");
                    }
                    writer.close();

                    //Weka example
                    Instances data = ConverterUtils.DataSource.read("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Combination"+flag+"\\"+patient.ID+"\\"+timePoint.ID+".arff");
                    SimpleKMeans kMeans = new SimpleKMeans();
                    kMeans.setNumClusters(1);
                    kMeans.buildClusterer(data);

                    // print out the cluster centroids
                    Instances centroid = kMeans.getClusterCentroids();
                    timePoint.centroid = new double[centroid.numInstances()];

                    for (int i = 0; i < centroid.numInstances(); i++) {
                        timePoint.centroid[i] = Double.parseDouble (centroid.instance(i).toString());
                    }
                    /*

                    double[][] points = new double[timePoint.samples.size()][h.size()];
                    int i = 0;
                    //int n = timePoint.samples.size();
                    for(Patient.TimePoint.Sample sample: timePoint.samples){
                        for(int m=0; m<sample.values.length ;m++){
                            points[i][m] = sample.values[m];
                        }
                        i++;
                    }
                    double centroids[][] = new double[1][h.size()];
                    for(int m=0; m<1 /*Can change the value from 1 to k if k-means;m++){
                        for(int n=0; n<h.size();n++)
                            centroids[m][n] = Math.abs(random.nextInt() % 10);
                    }
                    EKmeans eKmeans = new EKmeans(centroids, points);
                    eKmeans.run();
                    for(int m=0; m<h.size();m++)
                        timePoint.centroid[m] = centroids[0][m];*/
                }

            }


/*
            //Actual calculations
            for(int i=0; i<k; i++) {
                for (Patient patient : patients) {
                    double sum = 0;
                    for (int m = 0; m < patient.tp.size() - 1*/
/* for iterating till tn-1 and tn*//*
; m++) {
                       double dist1 = eucliedianDistance(patient.tp.get(m+1).centroid,centroids_pop[i]);
                       double dist2 = eucliedianDistance(patient.tp.get(m).centroid,centroids_pop[i]);
                       sum+=dist1-dist2;
                    }
                    patient.resultDelta = sum;
                }
                System.out.println("+++++++++++++++++++++++++++++++++++++For Attractor : "+(i+1)+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("Attractor:");
                for(int n=0; n<centroids_pop[i].length;n++)
                    System.out.print(centroids_pop[i][n]+",");
                System.out.println();
                System.out.println();
                System.out.println("Patient Code, Result");
                for (Patient patient : patients){
                    System.out.println(patient.ID+", "+patient.resultDelta);
                }

           }
*/



            //Test for eucliedianDistance
            System.out.println(eucliedianDistance( new double[]{0,0,0},new double[]{4,3,5}));

        }catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

                if (br1 != null)
                    br.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
  }

    public static double eucliedianDistance(double[] point1, double[] point2){
        double sum=0;
        for(int i=0; i< point1.length;i++){
            sum+= Math.pow((point2[i] - point1[i]),2);
        }
        return Math.sqrt( sum );


    }

    public static int combination(int n, int k)
    {
        return permutation(n) / (permutation(k) * permutation(n - k));
    }

    public static int permutation(int i)
    {
        if (i == 1)
        {
            return 1;
        }
        return i * permutation(i - 1);
    }
}
