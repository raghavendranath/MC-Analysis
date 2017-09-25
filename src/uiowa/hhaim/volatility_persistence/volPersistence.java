package uiowa.hhaim.volatility_persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.math.plot.*;

import javax.swing.*;

/**
 * Created by kandula on 9/19/2017.
 */
class Patient {
    String ID;
    ArrayList<TimePoint> tp;

    //To keep track of averages before TP+1
    ArrayList<Double> avg;
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
        this.avg = new ArrayList<>();
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
public class volPersistence {
    private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\volatility persistence\\data.txt";
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

            String[] temp = result.get(0);
            HashMap<String, Integer> h = new HashMap<>();
            for(int i=3; i< temp.length; i++){
                if(!h.containsKey(temp[i]))
                    h.put(temp[i],i-3);
            }

            result.remove(0);

            ArrayList<Patient> patients = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>();
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


            for(Patient pat:patients){
                double sum[] = new double[pat.tp.get(0).values.size()];
                for(int i=0; i<(pat.tp.size()-1) ; i++)
                    for(int j = 0; j< sum.length; j++)
                        sum[j] = sum[j] + pat.tp.get(i).values.get(j);
                for(int i=0; i< sum.length; i++)
                    pat.avg.add(sum[i]/(pat.tp.size()-1));
            }
            String position = "413"; //Change it for position
            double[] size = new double[patients.size()]; //for getting the maximum time point of all patients
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
                return;
            for(int k = 1; k<max ; k++){
                for(int j = k+1 ; j< max; j++){
                    //System.out.println((max-(k-1))+","+(max-(j-1)));

                    for(int i= 0; i< patients.size(); i++) {
                        int tp_size = patients.get( i ).tp.size();
                        if ((tp_size - j >= 0) && (tp_size -k >= 0)) {
                            int diff = Integer.parseInt( patients.get(i).tp.get(tp_size - k).ID ) - Integer.parseInt( patients.get( i ).tp.get( tp_size - j ).ID );
                            System.out.println( patients.get(i).ID+","+diff + "," + patients.get(i).tp.get(tp_size - k).values.get( h.get( position ) ) + "," + patients.get( i ).tp.get( tp_size - j).values.get( h.get( position ) ) );
                        }
                    }
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
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


            System.out.println();

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
