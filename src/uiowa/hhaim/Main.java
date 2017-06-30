package uiowa.hhaim;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Patient{
    private String ID;
    class Sample {
        String[] aa = new String[5]; //aa = aminoacids
        double[] values = new double[5];

        //Likelihood for a position for 19 other amino acids.
        double[][] likelihood = new double[20][5];
        double[] likelihoodMean = new double[5];
    }
    Sample[] samples = new Sample[10];
    private void createSamples(){
        for(int i=0; i<10;i++)
            this.samples[i] = new Sample();
    }
    double[] mean = new double[5];
    double[] likelihoodMean = new double[5];
    Patient(String ID){
        this.ID = ID;
        createSamples();
    }
}
public class Main {
    private static final String HIVwhivw = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\MC analysis\\Tab delimited files\\HIVw Hyphy Matrix.txt";
    private static final String ScalingTable = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\MC analysis\\Tab delimited files\\2F5_Scaling_Table.txt";
    private static final String Data_2F5 = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\MC analysis\\Tab delimited files\\2F5_Data.txt";
    private static final int POSITIONS = 5;
    private static final double ConstantK = 0.0;
    private static final double VI662 = 0.0;
    private static final double VI663 = 0.0;
    private static final double VI664 = 0.0;
    private static final double VI665 = 0.0;
    private static final double VI667 = 0.0;

    public static void main(String[] args) {
        // write your code here
        BufferedReader br = null, br1 = null, br2= null;
        FileReader fr = null, fr1 = null, fr2 = null;

        try {

            fr = new FileReader(HIVwhivw);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(HIVwhivw));
            ArrayList<String[]> result = new ArrayList<>();

            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine.trim().split("\t"));
            }
            HashMap<String,Integer> map = new HashMap<>();
            for(int i=0; i<result.size()-1; i++){
                if(!map.containsKey(result.get(0)[i])){
                    map.put(result.get(0)[i], i);
                 }
            }
            double[][] hivw = new double[map.size()][map.size()];
            int row = 0;
            int column = 0;
            double value = 0.0d;
            for(int i = 1; i< result.size(); i++){
                String[] data;
                data = result.get(i);
                for(int j = 1; j< data.length; j++){
                    hivw[i-1][j-1] = Double.parseDouble(data[j]);
                }
            }
            fr1 = new FileReader(ScalingTable);
            br1 = new BufferedReader(fr1);
            result = new ArrayList<>();
            while ((sCurrentLine = br1.readLine()) != null) {
                result.add(sCurrentLine.trim().split("\t"));
            }
            double[][] scaling = new double[map.size()][POSITIONS];
            for(int i = 1; i< result.size(); i++) {
                String[] data;
                data = result.get(i);
                for(int j = 1; j< data.length; j++){
                    if(j <= POSITIONS) {
                        scaling[i - 1][j - 1] = Double.parseDouble(data[j]);
                    }
                }
            }
            fr2 = new FileReader(Data_2F5);
            br2 = new BufferedReader(fr2);
            result = new ArrayList<>();
            while ((sCurrentLine = br2.readLine()) != null) {
                result.add(sCurrentLine.trim().split("\t"));
            }
            ArrayList<Patient> patients = new ArrayList<>();
            int j=-1;
            String previous = "Empty";
            for(int i = 1, k = -1; i< result.size(); i++){
                String[] data;
                data = result.get(i);
                if(previous.compareTo(data[0]) != 0){
                    patients.add(new Patient(data[0]));
                    previous = data[0];
                    j++;
                    k = 0;
                }
                int m = 0;
                double sum = 0.0d;
                for(String e:data){
                    patients.get(j).samples[k].aa[m] = data[m+1];
                    patients.get(j).samples[k].values[m] = scaling[map.get(data[m+1])][m];
                    m++;
                    if(m == 5)
                        break;
                }
                k++;
            }

            for(Patient person: patients ){
                //for each amino acid
                double[] sum_p_aa = new double[5];
                //for each likelihood
                double[] sum_p_ll = new double[5];
                for(Patient.Sample sample: person.samples){
                    for(int i=0; i<5; i++){
                        double sum_s = 0.0d;
                        double mean_s = 0.0d;
                        sum_p_aa[i] = sum_p_aa[i]+sample.values[i];
                         for(int k=0; k<20 ; k++){
                            double hivwValue = hivw[map.get(sample.aa[i])][k];
                            sample.likelihood[k][i] = hivwValue*(scaling[k][i] - sample.values[i]);
                            sum_s = sum_s+ hivwValue;
                            mean_s = mean_s+ sample.likelihood[k][i];
                            //sample.likelihoodMean[i] = sample.likelihoodMean[i]+ sample.likelihood[k][i];
                        }
                        sample.likelihoodMean[i] = mean_s/sum_s;
                        sum_p_ll[i] = sum_p_ll[i]+ sample.likelihoodMean[i];
                   }
                }
                for(int i=0 ;i<5;i++){
                    person.mean[i] = sum_p_aa[i]/10;
                    person.likelihoodMean[i] = sum_p_ll[i]/10;
                }
            }
            //all patients mean and standard deviation
            double[] patients_mean = new double[5];
            double[] patients_sd = new double[5];

            //all patients likelihood mean and standard deviation
            double[] patients_ll_mean = new double[5];
            double[] patients_ll_sd = new double[5];
            for(Patient patient:patients){
                for(int i =0; i<5; i++){
                    patients_mean[i] = patients_mean[i]+patient.mean[i];
                    patients_ll_mean[i] = patients_ll_mean[i] + patient.likelihoodMean[i];
                }
            }
            for(int i =0; i<5; i++){
                patients_mean[i] = patients_mean[i]/patients.size();
                patients_ll_mean[i] = patients_ll_mean[i]/patients.size();
            }

            //for caculating variance;
            double[] sum_sd = new double[5];
            double[] sum_ll_sd = new double[5];
            for(Patient patient:patients){
                for(int i=0; i< 5; i++){
                    sum_sd[i] = sum_sd[i] + Math.pow((patient.mean[i]-patients_mean[i]),2);
                    sum_ll_sd[i] = sum_ll_sd[i] + Math.pow((patient.likelihoodMean[i]-patients_ll_mean[i]),2);
                }
            }
            for(int i =0; i<5; i++){
                sum_sd[i] = sum_sd[i]/patients.size();
                sum_ll_sd[i] = sum_ll_sd[i]/patients.size();
            }

            for(int i=0; i< 5;i++){
                patients_sd[i]= Math.sqrt(sum_sd[i]);
                patients_ll_sd[i] = Math.sqrt(sum_ll_sd[i]);
            }

            System.out.println("Hello");

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

                if (br1 != null)
                    br1.close();

                if (fr1 != null)
                    fr1.close();

                if (br2 != null)
                    br2.close();

                if (fr2 != null)
                    fr2.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }
}