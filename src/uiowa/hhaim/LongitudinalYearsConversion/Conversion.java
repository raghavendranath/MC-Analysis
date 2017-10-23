package uiowa.hhaim.LongitudinalYearsConversion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * File locations: ..\Raghav\Analysis\Longuitudinal Sequence\Years
 * B_LS_Years
 * B_LS_Tianbo
 */
public class Conversion {
    private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Longuitudinal Sequence\\Years\\B_LS_Years.txt";
    private static final String Datafile_query = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Longuitudinal Sequence\\Years\\B_LS_Tianbo.txt";

    public static void main(String args[]){
        BufferedReader br = null,  br1 = null;
        FileReader fr = null, fr1 = null;
        try {

            fr = new FileReader(Datafile);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(Datafile));
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine.trim().split("\t"));
            }
            result.remove(0);
            HashMap<String, String> hs = new HashMap<>(  );
            for(String[] data: result){
                if(!hs.containsKey( data[0] ))
                    hs.put(data[0], data[1]);
            }


            fr1 = new FileReader(Datafile_query);
            br1 = new BufferedReader(fr);


            br1 = new BufferedReader(fr1);
            System.out.println("hello");

            ArrayList<String[]> result1 = new ArrayList<>();
            while ((sCurrentLine = br1.readLine()) != null) {
                result1.add(sCurrentLine.trim().split("\t"));
            }
            result1.remove(0);
            for(String[] data:result1){
                if(hs.containsKey( data[0] ))
                    System.out.println(hs.get(data[0]));
                else
                    System.out.println(" ");
            }



        }
        catch (Exception e) {

            e.printStackTrace();

        } finally{

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

                if (br1 != null)
                    br1.close();

                if (fr1 != null)
                    fr1.close();


            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }

}
