package uiowa.hhaim.volatility_persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
                for(int i=3; i<data.length;i++)
                    tp_temp.values.add(Double.parseDouble(data[i]));
            }



            for(Patient pat:patients){
                double sum[] = new double[pat.tp.get(0).values.size()];
                for(int i=0; i<(pat.tp.size()-1) ; i++)
                    for(int j = 0; j< sum.length; j++)
                        sum[j] = sum[j] + pat.tp.get(i).values.get(j);
                for(int i=0; i< sum.length; i++)
                    pat.avg.add(sum[i]/(pat.tp.size()-1));
            }
            String position = "448"; //Change it for position
            double[] x = new double[patients.size()];
            double[] y = new double[patients.size()];
            int i = 0;

            for(Patient pat: patients){
                x[i] = pat.avg.get(h.get(position));
                y[i] = pat.tp.get(pat.tp.size()-1).values.get(h.get(position));
                i++;
            }

            /*//Create a plotpanel
            Plot2DPanel plot = new Plot2DPanel(  );

            //add a line plot to the plotpanel
            plot.addLinePlot( "Voln vs Avg Vol" ,x,y);

            //Put the plotpanel in a JFrame, as a JPanel
            JFrame frame = new JFrame( "A plot panel" );
            frame.setContentPane( plot );
            frame.setVisible( true );

*/          for(i = 0; i<x.length; i++){
                System.out.println(x[i]+","+y[i]);
                }



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

}
