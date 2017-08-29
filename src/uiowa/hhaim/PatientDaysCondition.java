package uiowa.hhaim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kandula on 8/29/2017.
 * This Program is a map for the longitudinal data to the convergence data.
 * This program outputs the days of the convergence patients with respect to the values of patient and year in the longitudinal data
 */
class PatientDummy{
    ArrayList<String> years;
    HashMap<String, String> ht;
    String ID;
    PatientDummy(String datum){
        this.ID = datum;
        this.years = new ArrayList<>();
        this.ht = new HashMap<String, String>();
    }
}

public class PatientDaysCondition {
    private static final String actualfile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Convergence\\C_Days.txt";
    private static final String dataFile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Convergence\\C_temp.txt";
    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;
        BufferedReader br1 = null;
        FileReader fr1 = null;

        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader( new FileReader( actualfile ) );
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }
            result.remove(0);
            ArrayList<PatientDummy> patient = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>();
            for(String[] data: result) {
                if (!previous_patients.contains( data[0] )) {
                    patient.add( new PatientDummy( data[0] ) );
                    previous_patients.add( data[0] );
                }
                int pat_index = previous_patients.indexOf(data[0]);
                if(pat_index > patient.size())
                    break;
                PatientDummy patient_temp = patient.get(pat_index);
                if(patient_temp.ht == null || !patient_temp.ht.containsKey(data[1])){
                    patient_temp.years.add(data[1]);
                    patient_temp.ht.put(data[1],data[2]);
                }
            }

            br1 = new BufferedReader( new FileReader( dataFile ) );
            ArrayList<String[]> result1 = new ArrayList<>();
            while ((sCurrentLine = br1.readLine()) != null) {
                result1.add( sCurrentLine.trim().split( "\t" ) );
            }
            result1.remove(0);
            for(String[] data: result1) {
                int index = previous_patients.indexOf(data[0]);
                if(index > patient.size())
                    break;
                PatientDummy temp = patient.get(index);
                System.out.println(temp.ht.get(data[1]));
            }

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

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }
}
