package uiowa.hhaim;

import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.apache.commons.math3.stat.StatUtils.geometricMean;

/**
 * Created by kandula on 8/16/2017.
 */
public class GeneralGeoMean_Long {
    private static final String Datafile = "C:\\Users\\kandula.HEALTHCARE\\Desktop\\geo.txt";

    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;
        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader( new FileReader( Datafile ) );
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }
            /*
            ArrayList<Double> values = new ArrayList<>();
            for (String[] data : result) {
                if(data[0].equals( "NaN" ))
                    continue;
                values.add( Double.parseDouble( data[0] ) );
            }
            ArrayList<Double> minValues = new ArrayList<>();
            for (double number : values) {
                if (number != 0)
                    minValues.add( number );
            }
            //double min = Collections.min( minValues );
            //System.out.println( min );
            //min = min / 10;
            //for longitudinal data double min = 1.5664E-21;
            //for 5D volatility  double min = 0.001261028;

            //for positional
            double min = 1.13557E-07;

            double sum = 0.0 ;
            for(int i=0; i< minValues.size(); i++){
                sum+=minValues.get(i);
            }
            System.out.println(sum/values.size());
            for (int i = 0; i < values.size(); i++) {
                Double element = values.get( i );
                if (element == 0) {
                    values.remove( i );
                    values.add( i, min );
                }

            }
            double data[] = new double[values.size()];
            for(int i=0 ; i< values.size(); i++)
                data[i] = values.get(i);

            //System.out.println( GeneralGeoMean_Long.mean(data));
            System.out.println( StatUtils.geometricMean(data));
            //System.out.println( "Array" );
            */
            //for all positions at once
            for(int j=0; j<result.get(0).length; j++){
                ArrayList<Double> values = new ArrayList<>();
                for (String[] data : result) {
                    if(data[j].equals( "NaN" ))
                        continue;
                    values.add( Double.parseDouble( data[j] ) );
                }
                ArrayList<Double> minValues = new ArrayList<>();
                for (double number : values) {
                    if (number != 0)
                        minValues.add( number );
                }
                //double min = Collections.min( minValues );
                //System.out.println( min );
                //min = min / 10;
                //for longitudinal data double min = 1.5664E-21;
                //for 5D volatility  double min = 0.001261028;

                //for positional
                double min = 1.13557E-07;

                double sum = 0.0 ;
                for(int i=0; i< minValues.size(); i++){
                    sum+=minValues.get(i);
                }
                //System.out.println(sum/values.size());
                for (int i = 0; i < values.size(); i++) {
                    Double element = values.get( i );
                    if (element == 0) {
                        values.remove( i );
                        values.add( i, min );
                    }

                }
                double data[] = new double[values.size()];
                for(int i=0 ; i< values.size(); i++)
                    data[i] = values.get(i);

                //System.out.println( GeneralGeoMean_Long.mean(data));
                System.out.println( StatUtils.geometricMean(data));
            }
            //end of the program
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
