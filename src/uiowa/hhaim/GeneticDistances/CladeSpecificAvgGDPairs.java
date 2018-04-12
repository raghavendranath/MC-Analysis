package uiowa.hhaim.GeneticDistances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by kandula on 4/12/2018.
 */
public class CladeSpecificAvgGDPairs {
    public static void main(String args[]) {
        if (args.length == 2) {

            String fileLocation = args[0].replace( "\\", "\\\\" );
            BufferedReader br = null;
            FileReader fr = null;
            try {
                String sCurrentLine;

                br = new BufferedReader( new FileReader( fileLocation ) );
                ArrayList<String[]> buffer = new ArrayList<>();
                while ((sCurrentLine = br.readLine()) != null) {
                    buffer.add( sCurrentLine.trim().split( "\t" ) );
                }
            } catch (Exception e) {
                System.out.println( "Input file not found" );
                e.printStackTrace();
            } finally {
                try {

                    if (br != null)
                        br.close();

                    if (fr != null)
                        fr.close();


                } catch (Exception ex) {

                    ex.printStackTrace();

                }
            }

        }
    }
}


class CladeSpecific{
    String clade;
    ArrayList<Pair> envs;
    public CladeSpecific(String clade){
        this.clade = clade;
        envs = new ArrayList<>( );
    }

}

