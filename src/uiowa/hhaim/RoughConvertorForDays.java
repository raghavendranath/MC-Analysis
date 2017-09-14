package uiowa.hhaim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kandula on 8/30/2017.
 */
public class RoughConvertorForDays {
    private static final String actualfile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Convergence\\roughDays.txt";
    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;
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
            int flagCounter = 0;
            int prev = -1;
            for(String[] data: result){
                int temp = Integer.parseInt( data[0] );
                if(temp == 0) {
                    flagCounter = 1;
                    System.out.println( flagCounter );
                    prev = temp;
                    continue;
                }
                if(prev == temp) {
                    System.out.println( flagCounter );
                    continue;
                }
                if(temp > prev){
                    flagCounter++;
                    System.out.println(flagCounter);
                    prev = temp;
                    continue;
                }

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

