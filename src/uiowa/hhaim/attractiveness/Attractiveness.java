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
 */

import ca.pjer.ekmeans.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


class Patient {
    String ID;
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
    private static final String Datafile_popul = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Attractiveness\\Actual Data\\B_Population_Changes_2G12.txt";

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
            for (int i = 2; i < temp.length; i++) {
                if (!h.containsKey( temp[i] ))
                    h.put( temp[i], i - 2 );
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
                for(Patient.TimePoint timePoint: patient.tp){
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
                    for(int m=0; m<1 /*Can change the value from 1 to k if k-means*/;m++){
                        for(int n=0; n<h.size();n++)
                            centroids[m][n] = Math.abs(random.nextInt() % 10);
                    }
                    EKmeans eKmeans = new EKmeans(centroids, points);
                    eKmeans.run();
                    for(int m=0; m<h.size();m++)
                        timePoint.centroid[m] = centroids[0][m];
                }
            }

            //for getting population level centroids

            br1 = new BufferedReader(new FileReader(Datafile_popul));
            ArrayList<String[]> result1 = new ArrayList<>();
            while ((sCurrentLine = br1.readLine()) != null) {
                result1.add( sCurrentLine.trim().split( "\t" ) );
            }
            result1.remove(0);
            double[][] pop_points = new double[883][h.size()];
            int i_pop = 0;
            for(String[] data: result1){
                for(int m=0; m<h.size();m++)
                    pop_points[i_pop][m] = Double.parseDouble(data[m]);
                i_pop++;
            }
            int k = 5;
            double[][] centroids_pop = new double[k][h.size()];
            for (int i = 0; i < k; i++) {
                for(int j=0; j< h.size(); j++){
                    centroids_pop[i][j] = Math.abs( random.nextInt() % 100 );
                }
            }
            EKmeans eKmeans = new EKmeans(centroids_pop, pop_points);
            eKmeans.run();

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Population level centroids are:");
            for (int i = 0; i < k; i++) {
                for(int j=0; j<h.size();j++){
                    System.out.print(centroids_pop[i][j]+",");
                }
                System.out.println();

            }



            //Actual calculations

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


        int n = 4; // the number of data to cluster
        int k = 1; // the number of cluster
        Random random = new Random(System.currentTimeMillis());
        double[][] points = {  {  0.70,  0.75, 0.70, 0.75, 0.80 },
                               {  0.55,  0.30,  0.20, 0.10, 0.70},
                               { 0.80,  0.10,  0.00, 0.00, 0.80 },
                               { 0.70,  0.00,  0.00, 0.00, 0.80 },
                               { 0.80,  0.90,  0.80, 0.75, 0.80 }
                              };
// lets create random centroids between 0 and 100 (in the same space as our points)
        double[][] centroids = new double[k][5];
        System.out.println("Centroid before :");
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < k; i++) {
            centroids[i][0] = Math.abs(random.nextInt() % 100);
            centroids[i][1] = Math.abs(random.nextInt() % 100);
            centroids[i][2] = Math.abs(random.nextInt() % 100);
            centroids[i][3] = Math.abs(random.nextInt() % 100);
            centroids[i][4] = Math.abs(random.nextInt() % 100);
            System.out.println("Centroid "+i+":"+ centroids[i][0]+","+centroids[i][1] );
        }
        EKmeans eKmeans = new EKmeans(centroids, points);
        eKmeans.run();
        System.out.println("Centroid after :");
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < k; i++) {
            System.out.println("Centroid "+i+":"+ centroids[i][0]+","+centroids[i][1] );
        }
        int[] assignments = eKmeans.getAssignments();

        System.out.println(eKmeans.getCentroids()[0][0]+","+eKmeans.getCentroids()[0][1]);
// here we just print the assignement to the console.
/*        for (int i = 0; i < n; i++) {
            System.out.println( MessageFormat.format("point {0} is assigned to cluster {1}", i, assignments[i]));
        }*/
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
