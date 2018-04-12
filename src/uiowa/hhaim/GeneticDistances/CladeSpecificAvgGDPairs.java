package uiowa.hhaim.GeneticDistances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kandula on 4/12/2018.
 */
public class CladeSpecificAvgGDPairs {
    public static void main(String args[]) {
        if (args.length != 2){
            System.out.println("Give [Genetic distances computed file location] in the arguments and output file location");
            System.exit(0);
        }
        else{

            String fileLocation = args[0].replace( "\\", "\\\\" );
            BufferedReader br = null;
            FileReader fr = null;
            try {
                String sCurrentLine;

                br = new BufferedReader( new FileReader( fileLocation ) );
                ArrayList<String[]> buffer = new ArrayList<>();
                HashSet<String> cladeValues  = new HashSet<>();
                String[] temp = null;
                while ((sCurrentLine = br.readLine()) != null) {
                    temp = sCurrentLine.trim().split( "\t" );
                    if(!cladeValues.contains( temp[0] ))
                        cladeValues.add( temp[0] );
                    buffer.add( temp );
                }

                //Removing headers
                buffer.remove(0);

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

