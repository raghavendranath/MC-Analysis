package uiowa.hhaim.GeneticDistances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kandula on 4/10/2018.
 */
public class MatrixToPairs {
    public static void main(String args[]){
        BufferedReader br = null;
        FileReader fr = null;
        if(args.length != 2){
            System.out.println("Give [input file location(tsv)] and [outputfile location(.txt)]. Remove spaces in filenames");
            System.exit(1);
        }
        final String fileLocation = args[0].replace("\\","\\\\");;
        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);
            String sCurrentLine;

            br = new BufferedReader( new FileReader( fileLocation ) );
            ArrayList<String[]> buffer = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                buffer.add( sCurrentLine.trim().split( "\t" ) );
            }

            HashMap<Integer, String> reverseMap = new HashMap<>(  );
            int index = 0;
            String patDetails[] = buffer.get(0);
            for(String patientTP: patDetails){
                if(!reverseMap.containsKey( patientTP )) {
                    reverseMap.put(index++,patientTP);
                }
            }
            buffer.remove(0);
            int matIndex = 1;
            ArrayList<PairMat> distPairs = new ArrayList<>(  );
            for(String[] data: buffer){
               for(int i=matIndex; i<data.length-1; i++){
                   PairMat newPair = new PairMat(data[0],reverseMap.get(i) );
                   //removing the same env sample results as they are not necessary
                   if(newPair.env1.equals( newPair.env2 ))
                       continue;
                   int pairIndex = PairMat.getIndexOfPair( distPairs, newPair );
                   if (pairIndex != -1) {
                       continue;
                   } else {
                       newPair.distance = data[i+1];
                       distPairs.add(newPair);

                   }
               }
               matIndex++;
            }

            PrintWriter writer = new PrintWriter(args[1].replace("\\","\\\\"));
            writer.append("Env1\tEnv2\tJaccard Dist\n");
            for(PairMat pat: distPairs){

                writer.append(pat.env1+"\t"+pat.env2+"\t"+pat.distance);
                writer.append("\n");
            }
            writer.close();
            System.out.println("Computations accomplished");

        }
        catch(Exception e){
            System.out.println("Input file not found");
            e.printStackTrace();
        }
        finally{
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

class PairMat{
    String env1;
    String env2;
    String distance;
    PairMat(String env1, String env2){
        this.env1 = env1;
        this.env2 = env2;
        distance = null;
    }
    static int getIndexOfPair(ArrayList<PairMat> pairs, PairMat newPair){
        for(int i=0; i<pairs.size(); i++){
            PairMat temp = pairs.get(i);
            if(temp.env1.equals( newPair.env1 ) && temp.env2.equals( newPair.env2 ))
                return i;
        }
        return -1;
    }

}



