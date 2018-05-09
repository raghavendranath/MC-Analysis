package uiowa.hhaim.volatility_persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.math.plot.*;

import javax.swing.*;

/**
 * Created by kandula on 9/19/2017.
 * NoOfEnvs, Patient, Days, All positions(Vol values) -> Input file format (tab delimited)
 * format data into pre-defined data structure to be used for later calculations
 * no calculation done, yet.
 */
class Patient {
    String ID;
    ArrayList<TimePoint> tp;

/*    //To keep track of averages before TP+1
    ArrayList<Double> avg; //It's not used at this moment.*/
    static class TimePoint{
        String ID;
        String numOfEnvs;
        ArrayList<Double> values;
        TimePoint(String ID, String numOfEnvs){
            this.ID = ID;
            this.numOfEnvs = numOfEnvs;
            values = new ArrayList<>();
        }
    }


    Patient(String ID) {
        this.ID = ID;
        this.tp = new ArrayList<>();
        //this.avg = new ArrayList<>();
    }

    //Code used for checking if the timepoint is present for that patient
    boolean contains(String dayTP){
        if(tp.isEmpty())
            return false;
        for(TimePoint time:tp ){
            if(dayTP.equals( time.ID ))
                return true;
        }
        return false;
    }
    // getting index of timepoint
    int getTimePointIndex(String dayTP){
        for(int i=0; i< tp.size(); i++){
            if(dayTP.equals( tp.get(i).ID ))
                return i;
        }
        return -1;
    }
}

//For timepoint comparision, sorting patient timepoint in ascending order
class TimePointCompare implements Comparator<Patient.TimePoint>{
    public int compare(Patient.TimePoint o1, Patient.TimePoint o2){
        int tp1 = Integer.parseInt(o1.ID);
        int tp2 = Integer.parseInt(o2.ID);
        if (tp1 < tp2) return -1;
        if (tp1 > tp2) return 1;
        else return 0;
    }
}

public class volPersistence {
    //private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\volatility persistence\\data.txt";
    //private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\volatility persistence\\5D volatility\\data_AE.txt";
    //private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Hepatitis C\\Volatility\\Results\\Volatiliy_Persistence_3andAcute.txt";
    // input file
    private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Volatility Forecasting Manuscript\\Fig 2 Volatility Persistence\\Temp_ForCode\\B_LS_data.txt";

    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;

        try {
            String sCurrentLine;
            br = new BufferedReader( new FileReader( sheet ) );
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }

            // header, put all positions into map
            String[] temp = result.get(0);
            HashMap<String, Integer> h = new HashMap<>();
            for(int i=3; i< temp.length; i++){
                if(!h.containsKey(temp[i]))
                    h.put(temp[i],i-3);
            }

            result.remove(0);

            ArrayList<Patient> patients = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>(); // avoid creating duplicate patients
            for(String[] data: result) {
                if (!previous_patients.contains( data[1] )) {
                    patients.add( new Patient( data[1] ) );
                    previous_patients.add( data[1] );
                }
                int pat_index = previous_patients.indexOf( data[1] );
                if (pat_index > patients.size())
                    break;
                Patient patient_temp = patients.get( pat_index );
                if(!patient_temp.contains( data[2] )) {
                    patient_temp.tp.add( new Patient.TimePoint(data[2],data[0]));
                }
                int tp_index = patient_temp.getTimePointIndex( data[2] );
                if(tp_index == -1)
                    break;
                Patient.TimePoint tp_temp = patient_temp.tp.get(tp_index);
                for(int i=3; i<data.length;i++) {
                    if (Double.parseDouble( data[i] ) == 0)
                        //changing value 0 to 0.001
                        data[i] = "0.001";
                    tp_temp.values.add( Double.parseDouble(data[i]));
                }
            }


            // sorts asceding order for timepoints in each patient
            TimePointCompare tpcomparator = new TimePointCompare();
            for(Patient pat: patients){
                Collections.sort(pat.tp, tpcomparator);
            }


            System.out.println();

/*
            for(Patient pat:patients){
                double sum[] = new double[pat.tp.get(0).values.size()];
                for(int i=0; i<(pat.tp.size()-1) ; i++)
                    for(int j = 0; j< sum.length; j++)
                        sum[j] = sum[j] + pat.tp.get(i).values.get(j);
                for(int i=0; i< sum.length; i++)
                    pat.avg.add(sum[i]/(pat.tp.size()-1));
            }*/
            //String position = "5D(2F5)"; //Change it for position
            //String[] positions = {"555", "616"}; //Give all positions you want
            //Glycan patch, MPER, TAD (Trimer association domain)
            String[] positions = {"295","332","339","392","386","448","412","413","137","301","327","363","662","663","664","665","666","667","668","669","670","671","672","673","674","675","676","677","678","679","680","681","682","683","156","160","165","167","168","169","170","171","173"};

/*            double[] size = new double[patients.size()]; //for getting the maximum time point of all patients
            int index = 0;
            int max = 0;
            for(Patient pat: patients){
                int t = pat.tp.size();
                if(max < t)
                    max = t;
                size[index++] = t;
            }

            //Number of combinations
            int noOfComb = (int)choose( max ,2 );
            //System.out.println(noOfComb);
            if(noOfComb == 0)
                return;*/

            PrintWriter writer = new PrintWriter("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Volatility Forecasting Manuscript\\Fig 2 Volatility Persistence\\Temp_ForCode\\B_LS_Output_VolPersis.txt");
            //System.out.print("Patient,∆T,");
            writer.append("Patient,∆T,");
            for(String pos: positions)
                //System.out.print(pos+"Vn"+","+pos+"Vn-1"+",");
                writer.append(pos+"Vn"+","+pos+"Vn-1"+",");

            //System.out.println();
            writer.append( "\n" );

            //Old method - everything with everything
            /*
            for(int k = 1; k<max ; k++){
                //System.out.println("Hello");
                for(int j = k+1 ; j< max; j++){
                    //System.out.println((max-(k-1))+","+(max-(j-1)));

                    for(int i= 0; i< patients.size(); i++) {
                        int tp_size = patients.get( i ).tp.size();
                        if ((tp_size - j >= 0) && (tp_size -k >= 0)) {
                            int diff = Integer.parseInt( patients.get(i).tp.get(tp_size - k).ID ) - Integer.parseInt( patients.get( i ).tp.get( tp_size - j ).ID );
                            //System.out.print( patients.get(i).ID+","+diff + ",");
                            writer.append( patients.get(i).ID+","+diff + ",");
                            for(int pos = 0; pos < positions.length; pos++){
                                //System.out.print( patients.get(i).tp.get(tp_size - k).values.get( h.get( positions[pos] ) ) + "," + patients.get( i ).tp.get( tp_size - j).values.get( h.get( positions[pos] ) ) +",");
                                writer.append( patients.get(i).tp.get(tp_size - k).values.get( h.get( positions[pos] ) ) + "," + patients.get( i ).tp.get( tp_size - j).values.get( h.get( positions[pos] ) ) +",");;
                            }
                            //System.out.println();
                            writer.append("\n");
                        }

                    }
                    //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
                }

            }
*/
            for(Patient pat: patients){
                int tpSize = pat.tp.size();
                if(tpSize < 2)
                    continue;
                //Let us assume A,B,C,D are timepoints for even
                //If even, then we should do BA, DC (two strides for all timepoints)
                if(tpSize % 2 == 0){
                    for(int i=0; i< pat.tp.size(); i=i+2){
                        int diff = Integer.parseInt(pat.tp.get(i+1).ID) - Integer.parseInt(pat.tp.get(i).ID);
                        //System.out.print( pat.ID+","+diff + ",");
                        writer.append( pat.ID+","+diff + ",");
                        for(int pos = 0; pos < positions.length; pos++){
                            //System.out.print( pat.tp.get(i+1).values.get( h.get( positions[pos] ) ) + "," + pat.tp.get(i).values.get( h.get( positions[pos] ) ) +",");
                            writer.append( pat.tp.get(i+1).values.get( h.get( positions[pos] ) ) + "," + pat.tp.get(i).values.get( h.get( positions[pos] ) ) +",");
                        }
                        //System.out.println();
                        writer.append("\n");
                    }
                }

                //Let us assume A,B,C,D,E are timepoints for even
                //If odd, then we should do BA, DC, ED
                // two strides till second from last timepoint, do 1 stride for last timepoint
                else{
                    int j=0;
                    for(; j< pat.tp.size()-1; j=j+2){
                        int diff = Integer.parseInt(pat.tp.get(j+1).ID) - Integer.parseInt(pat.tp.get(j).ID);
                        //System.out.print( pat.ID+","+diff + ",");
                        writer.append( pat.ID+","+diff + ",");
                        for(int pos = 0; pos < positions.length; pos++){
                            //System.out.print( pat.tp.get(j+1).values.get( h.get( positions[pos] ) ) + "," + pat.tp.get(j).values.get( h.get( positions[pos] ) ) +",");
                            writer.append( pat.tp.get(j+1).values.get( h.get( positions[pos] ) ) + "," + pat.tp.get(j).values.get( h.get( positions[pos] ) ) +",");
                        }
                        //System.out.println();
                        writer.append("\n");
                    }

                    //The last one
                    int diff = Integer.parseInt(pat.tp.get(j).ID) - Integer.parseInt(pat.tp.get(j-1).ID);
                    writer.append( pat.ID+","+diff + ",");
                    for(int pos = 0; pos < positions.length; pos++){
                       writer.append( pat.tp.get(j).values.get( h.get( positions[pos] ) ) + "," + pat.tp.get(j-1).values.get( h.get( positions[pos] ) ) +",");
                    }
                    writer.append("\n");
                }

            }

            /*//Create a plotpanel
            Plot2DPanel plot = new Plot2DPanel(  );

            //add a line plot to the plotpanel
            plot.addLinePlot( "Voln vs Avg Vol" ,x,y);

            //Put the plotpanel in a JFrame, as a JPanel
            JFrame frame = new JFrame( "A plot panel" );
            frame.setContentPane( plot );
            frame.setVisible( true );

*/ /*         for(i = 0; i<x.length; i++){
                System.out.println(x[i]+","+y[i]);
                }
*/

            writer.close();
            System.out.println("Computations Accomplished without any error!");

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
    public static double choose(int x, int y) {
        if (y < 0 || y > x) return 0;
        if (y > x/2) {
            // choose(n,k) == choose(n,n-k),
            // so this could save a little effort
            y = x - y;
        }

        double denominator = 1.0, numerator = 1.0;
        for (int i = 1; i <= y; i++) {
            denominator *= i;
            numerator *= (x + 1 - i);
        }
        return numerator / denominator;
    }

}