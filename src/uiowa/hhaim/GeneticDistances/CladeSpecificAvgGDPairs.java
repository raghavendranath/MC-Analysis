package uiowa.hhaim.GeneticDistances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kandula on 4/12/2018.
 */
public class CladeSpecificAvgGDPairs {
    public static void main(String args[]) {
        if (args.length != 2){
            System.out.println("Give [Genetic distances computed file location] in the arguments and output file location(without a .txt extension)");
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
                String[] line;
                String temp; //To carry the clade values
                boolean isHeader = true;
                while ((sCurrentLine = br.readLine()) != null) {
                    line = sCurrentLine.trim().split( "\t" );
                    if(isHeader){
                         isHeader = false;
                         //Removing the header info initially itself
                        continue;
                    }
                    temp = line[0].split( "\\." )[0];
                    if(!cladeValues.contains(temp))
                        cladeValues.add(temp);
                    temp = line[1].split( "\\." )[0];
                    if(!cladeValues.contains(temp))
                        cladeValues.add(temp);
                    buffer.add( line );
                }


                ArrayList<CladeSpecific> clades = new ArrayList<>(  );
                for(String cladeVal: cladeValues){
                    clades.add(new CladeSpecific( cladeVal ));
                }

                String[] seq1 = null, seq2 = null;
                for(String[] data: buffer) {
                    if (data.length != 3) {
                        System.out.println( "Wrongly formatted input file" );
                        System.exit( 0 );
                    }
                    seq1 = data[0].split( "\\." );
                    seq2 = data[1].split( "\\." );
                    if(seq1.length < 3 && seq2.length < 3 ){
                        System.out.println("Inaccurate data split");
                        System.out.println(data);
                        System.exit( 0 );
                    }

                    if(!seq1[0].equals( seq2[0] ))
                        continue;
                    CladeSpecific clade = CladeSpecific.getClade(clades, seq1[0]);
                    if(clade == null){
                        System.out.println("Clades are not loaded in the program properly. Debug it");
                        System.exit(0);
                    }
                   Pair newPair = new Pair( seq1[2] + "." + seq1[3], seq2[2] + "." + seq2[3] );
                    //removing the same env sample results as they are not necessary
                    if(newPair.env1.equals( newPair.env2 ))
                        continue;
                    int index = Pair.getIndexOfPair( clade.envs, newPair );
                    if (index != -1) {
                        newPair = clade.envs.get( index );
                        newPair.distances.add( Double.parseDouble( data[2] ) );
                    } else {
                        newPair.distances.add( Double.parseDouble( data[2] ) );
                        clade.envs.add( newPair );
                    }
                }
                String outputLocation = args[1].replace("\\","\\\\");

                //Calculating the average
                for(CladeSpecific clade: clades){
                    PrintWriter writer = new PrintWriter( outputLocation+"_"+clade.name+".txt");
                    writer.append("Env1\tEnv2\tAverageGD\n");
                    for(Pair pair: clade.envs){
                        pair.calculateMean();
                        writer.append(pair.env1+"\t"+pair.env2+"\t"+pair.average+"\n");
                    }
                    writer.close();
                }
                System.out.println("Computations accomplished without any errors!");




            } catch (Exception e) {
                //System.out.println( "Input file not found" );
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
    String name;
    ArrayList<Pair> envs;
    public CladeSpecific(String clade){
        name = clade;
        envs = new ArrayList<>( );
    }

    public static CladeSpecific getClade(ArrayList<CladeSpecific> cladeDetails, String getClade ){
        for(CladeSpecific clade: cladeDetails){
            if(clade.name.equals( getClade ))
                return clade;
        }
        return null;
    }

}

