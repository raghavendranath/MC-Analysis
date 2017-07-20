package uiowa.hhaim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kandula on 7/20/2017.
 */

class Patient1{
    String ID;
    Patient1(String ID){
        this.ID = ID;
        this.years = new ArrayList<>();
        this.yearIndex = new ArrayList<>();
    }
    public static class Year{
        String year;
        Year(String year){
            this.year = year;
            this.samples = new ArrayList<>();
            this.sampleIndex = new ArrayList<>();
        }

        ArrayList<Sample> samples;
        ArrayList<String> sampleIndex;
        public static class Sample{
            ArrayList<String> aminoacids = new ArrayList<>();
            ArrayList<Double> values = new ArrayList<>();
            Sample(ArrayList<String> aminoacids, ArrayList<Double> values){
                this.aminoacids = aminoacids;
                this.values = values;
            }
        }
    }

    ArrayList<Year> years;
    ArrayList<String> yearIndex;

}
public class Longitudinal {
    private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Longuitudinal Sequence\\Code\\C_LS.txt";
    public static void main(String args[]){
        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(Datafile);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(Datafile));
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine.trim().split("\t"));
            }
            ArrayList<String> positions = new ArrayList<>();
            for(int i=2; i<result.get(0).length;i++){
                positions.add(result.get(0)[i]);
            }
            result.remove(0);
            ArrayList<Patient1> patient = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>();
            for(String[] data: result){
                if(!previous_patients.contains(data[1])){
                    patient.add(new Patient1(data[1]));
                    previous_patients.add(data[1]);
                }
                int pat_index = previous_patients.indexOf(data[1]);
                if(pat_index > patient.size())
                    break;
                Patient1 patient_temp = patient.get(pat_index);
                if(patient_temp.yearIndex == null || !patient_temp.yearIndex.contains(data[0])){
                    patient_temp.years.add(new Patient1.Year(data[0]));
                    patient_temp.yearIndex.add(data[0]);
                 }
                int year_index = patient_temp.yearIndex.indexOf(data[0]);
                Patient1.Year year_temp = patient_temp.years.get(year_index);
                ArrayList<String> aa = new ArrayList<>();
                ArrayList<Double> values = new ArrayList<>();
                for(int i=2; i< data.length; i++){
                    aa.add(data[i]);
                    values.add(getVal(data[i]));
                }
                year_temp.samples.add(new Patient1.Year.Sample(aa,values));


            }

            //Printing
            System.out.print("Patient"+","+"Year");
            for(int i=0; i< positions.size();i++){
                System.out.print(","+positions.get(i));
            }
            System.out.println();

            for(Patient1 patients: patient){
                for(Patient1.Year year: patients.years){
                    double[] avg = new double[positions.size()];
                    for(Patient1.Year.Sample sample: year.samples){
                        System.out.print(patients.ID+","+year.year);
                        for(int i=0; i<sample.aminoacids.size();i++){
                            System.out.print(","+sample.aminoacids.get(i));
                        }
                        for(int i=0; i<sample.values.size();i++){
                            System.out.print(","+sample.values.get(i));
                            avg[i] = avg[i]+ sample.values.get(i);
                        }
                        System.out.println();
                    }
                    for(int i=0 ; i< avg.length; i++){
                        avg[i] = avg[i]/year.samples.size();
                    }
                    printArray(avg);

                }
            }


            System.out.println("Haalo");
        }
            catch (Exception e) {

                e.printStackTrace();

            } finally{

                try {

                    if (br != null)
                        br.close();

                    if (fr != null)
                        fr.close();

                } catch (IOException ex) {

                    ex.printStackTrace();

                }
            }
        }

        public static double getVal(String aa){
            switch(aa){
                case "A": return 0.68;
                case "C": return 0.733;
                case "D": return 0.19;
                case "E": return 0.203;
                case "F": return 1.00;
                case "G": return 0.584;
                case "H": return 0.304;
                case "I": return 0.958;
                case "K": return 0.403;
                case "L": return 0.953;
                case "M": return 0.782;
                case "N": return 0.363;
                case "P": return 0.759;
                case "Q": return 0.376;
                case "R": return 0.167;
                case "S": return 0.466;
                case "T": return 0.542;
                case "V": return 0.854;
                case "W": return 0.898;
                case "Y": return 0.90;
                case "Z": return 0.0;
                case "-": return 1.5;
                default : return 0.0;


            }

        }
    private static void printArray(double array[]){
        for(int i=0 ; i<5; i++) {
            if(i<4)
                System.out.print(array[i]+",");
            else
                System.out.print(array[i]);

        }
        System.out.println();
    }
}
