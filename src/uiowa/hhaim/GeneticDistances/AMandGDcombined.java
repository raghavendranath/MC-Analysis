package uiowa.hhaim.GeneticDistances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by kandula on 4/10/2018.
 * Remove all punctutations in Arithmetic
 */
public class AMandGDcombined {
    public static void main(String args[]){
        BufferedReader br = null;
        FileReader fr = null;
        BufferedReader br1 = null;
        FileReader fr1 = null;
        if(args.length != 3){
            System.out.println("Give two [input file location(tsv) - first file is Arithmetic GDs, and second file is Jaccord distance file] and a [outputfile location(.txt)]. Remove spaces in filenames");
            System.exit(1);
        }
        final String amLocation = args[0].replace("\\","\\\\");;
        final String jdLocation = args[1].replace("\\","\\\\");;
        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);
            String sCurrentLine;

            br = new BufferedReader( new FileReader( amLocation ) );
            ArrayList<String[]> buffer = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                buffer.add( sCurrentLine.trim().split( "\t" ) );
            }
            buffer.remove(0); //Removes the header
            ArrayList<PairAMJD> distPairs = new ArrayList<>(  );

            //First loop goes through Arithmetic means
            for(String[] data: buffer) {

                PairAMJD newPair = new PairAMJD(data[0],data[1] );
                if(newPair.env1.equals( newPair.env2 ))
                    continue;
                int index = PairAMJD.getIndexOfPair( distPairs, newPair );
                if (index != -1) {
                    continue;
                } else {
                    newPair.AMdist = data[2];
                    distPairs.add( newPair );
                }

            }

            br1 = new BufferedReader( new FileReader( jdLocation ) );
            buffer = new ArrayList<>();
            while ((sCurrentLine = br1.readLine()) != null) {
                buffer.add( sCurrentLine.trim().split( "\t" ) );
            }
            buffer.remove(0); //Removes the header

            //Second loop goes through JD distances
            for(String[] data: buffer){
                PairAMJD newPair = new PairAMJD(data[0],data[1] );
                if(newPair.env1.equals( newPair.env2 ))
                    continue;
                int index = PairAMJD.getIndexOfPair( distPairs, newPair );
                if(index == -1){
                    newPair =  new PairAMJD(data[1],data[0] );
                    index = PairAMJD.getIndexOfPair( distPairs, newPair );
                }
                if (index != -1) {
                    newPair = distPairs.get( index );
                    newPair.JDdist = data[2];

                } else {
                    newPair.JDdist = data[2];
                    distPairs.add( newPair );
                }


            }

            PrintWriter writer = new PrintWriter(args[2].replace("\\","\\\\"));
            writer.append("Env1\tEnv2\tMean GD\tJaccard Dist\n");
            for(PairAMJD pat: distPairs){

                writer.append(pat.env1+"\t"+pat.env2+"\t"+pat.AMdist+"\t"+pat.JDdist);
                writer.append("\n");
            }
            writer.close();

            System.out.println("Tasks accomplished");
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

                if (br1 != null)
                    br.close();

                if (fr1 != null)
                    fr.close();


            } catch (Exception ex) {

                ex.printStackTrace();

            }
        }


    }


}

class PairAMJD{
    String env1;
    String env2;
    String AMdist;
    String JDdist;
    PairAMJD(String env1, String env2){
        this.env1 = env1;
        this.env2 = env2;
        this.AMdist = "";
        this.JDdist = "";
    }

    static int getIndexOfPair(ArrayList<PairAMJD> pairs, PairAMJD newPair){
        for(int i=0; i<pairs.size(); i++){
            PairAMJD temp = pairs.get(i);
            if(temp.env1.equals( newPair.env1 ) && temp.env2.equals( newPair.env2 ))
                return i;
        }
        return -1;
    }
}