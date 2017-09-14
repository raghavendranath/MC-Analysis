package uiowa.hhaim.Convergence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by kandula on 8/31/2017.
 */

class Patient{
    String ID;
    ArrayList<TimePoint> tp;
    static class TimePoint{
        String ID; // timepoint
        String day; // actual day
        HashMap<String, Double> aminoAcids;
        double total; // for the count of Amino acids in a timepoints
        TimePoint(String id){
            this.ID = id;
            aminoAcids = new HashMap<>(  );
            //adding all amino acids to the hashmap
            char ch = 'A';
            for(int i=0; i<26;i++){
                if(ch != 'B' && ch!= 'J' && ch!= 'O' && ch!='U' && ch!= 'X') {
                    aminoAcids.put( Character.toString( ch ), 0d );
                }
                ch++;
            }

            aminoAcidsLogodds = new HashMap<>( );
            aminoAcidLogoddsComparison = new HashMap<>(  );
            total =0;
        }
        HashMap<String, Double> aminoAcidsLogodds;

        //for comparing proportion with the logodds of that proportion. (Point no:1 date: 9/11/2017
        HashMap<String,Double[]> aminoAcidLogoddsComparison;
    }
    Patient(String name){
        this.ID = name;
        this.tp = new ArrayList<>(  );

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
public class convergence_Analysis {
    private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Convergence\\ProgramData\\convergence.txt";
    final static private double v = 2.5;
    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader( new FileReader( sheet ) );
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }
            result.remove(0);
            ArrayList<Patient> patient = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>();
            for(String[] data: result) {
                if (!previous_patients.contains( data[0] )) {
                    patient.add( new Patient( data[0] ) );
                    previous_patients.add( data[0] );
                }
                int pat_index = previous_patients.indexOf( data[0] );
                if (pat_index > patient.size())
                    break;
                Patient patient_temp = patient.get( pat_index );
                if(!patient_temp.contains( data[1] )) {
                    patient_temp.tp.add( new Patient.TimePoint(data[1]));
                }
                int tp_index = patient_temp.getTimePointIndex( data[1] );
                if(tp_index == -1)
                    break;
                Patient.TimePoint tp_temp = patient_temp.tp.get(tp_index);
                /*
                if(!tp_temp.aminoAcids.containsKey( data[2] ))
                    tp_temp.aminoAcids.put(data[2],1d);
                    */
                tp_temp.aminoAcids.put(data[2], tp_temp.aminoAcids.get(data[2])+1);
                tp_temp.total++;
                tp_temp.day = data[3];
            }


            for(Patient pat: patient){
                for(Patient.TimePoint time: pat.tp){
                    Set set =time.aminoAcids.entrySet();
                    Iterator i = set.iterator();
                    while(i.hasNext()){
                        Map.Entry element = (Map.Entry)i.next();
                        double value = Double.parseDouble( element.getValue().toString());
                        /*double prob = value/time.total;
                        double probMin = 0.001d/time.total;
                        double probBar = (time.total - value)/time.total;

                        if(value == 0d){
                            time.aminoAcidsLogodds.put((String)element.getKey(),Math.log10(probMin/probBar));
                            continue;
                        }
                        if(time.total == value)
                            time.aminoAcidsLogodds.put((String)element.getKey(),Math.log10(prob/probMin));
                        else
                            time.aminoAcidsLogodds.put((String)element.getKey(),Math.log10(prob/probBar));*/

                        double prob = value/time.total;
                        //double probMin = 0.01d/time.total;
                        double probBar = (time.total - value)/time.total;
                        time.aminoAcidsLogodds.put((String)element.getKey(),Math.log10(((Math.exp(v*(prob-1))+0.3)/1.3)/((Math.exp(v*(probBar-1))+0.3)/1.3)));
                        //For comparison between the proportion and the formula
                        time.aminoAcidLogoddsComparison.put((String)element.getKey(),new Double[]{prob,((Math.exp(v*(prob-1))+0.3)/1.3)});
                    }
                }
            }
            
            String aminoAcidNeeded = "E"; //Change this depending on the amino acid you want
            
            for(Patient pat: patient){
                  int size = pat.tp.size();
                  for(int i=0; i<size-1;i++){
                      System.out.println(pat.ID+","+(i+2)+","+(i+1)+","+pat.tp.get(i+1).day+","+pat.tp.get(i).day+","+pat.tp.get(i+1).total+","+pat.tp.get(i+1).aminoAcidsLogodds.get(aminoAcidNeeded)+","+pat.tp.get(i).aminoAcidsLogodds.get(aminoAcidNeeded));
                  }

            }

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            for(Patient pat: patient){
                int size = pat.tp.size();
                for(int i=0; i<size;i++){
                    System.out.println(pat.ID+","+(i+1)+","+pat.tp.get(i).aminoAcidLogoddsComparison.get(aminoAcidNeeded)[0]+","+pat.tp.get(i).aminoAcidLogoddsComparison.get(aminoAcidNeeded)[1]);
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

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

}
