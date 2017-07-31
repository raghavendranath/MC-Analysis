package uiowa.hhaim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kandula on 7/31/2017.
 */
public class HydropathyLookup {
        //private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Longuitudinal Sequence\\Code\\B_LS_NPR.txt";
        private static final String Datafile = "C:\\Users\\kandula.HEALTHCARE\\Desktop\\europe.txt";
        public static void main(String args[]){
            BufferedReader br = null;
            FileReader fr = null;

            try {

                fr = new FileReader(Datafile);
                br = new BufferedReader(fr);

                String sCurrentLine;

                br = new BufferedReader(new FileReader(Datafile));
                ArrayList<String[]> result = new ArrayList<>();
                while ((sCurrentLine = br.readLine()) != null) {
                    result.add(sCurrentLine.trim().split("\t"));
                }
                ArrayList<String> positions = new ArrayList<>();
                for(int i=2; i<result.get(0).length;i++){
                    positions.add(result.get(0)[i]);
                }
                result.remove(0);
                for(String[] data: result){
                    for(String d: data){
                        System.out.print(d+",");
                    }
                    for(int i=2; i< data.length; i++){
                        System.out.print(getVal(data[i])+",");
                    }
                    System.out.println();
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

                } catch (IOException ex) {

                    ex.printStackTrace();

                }
            }
        }

        public static double getVal(String aa){
            switch(aa){
                case "A": return 0.68;
                case "C": return 0.733;
                case "D": return 0.19;
                case "E": return 0.203;
                case "F": return 1.00;
                case "G": return 0.584;
                case "H": return 0.304;
                case "I": return 0.958;
                case "K": return 0.403;
                case "L": return 0.953;
                case "M": return 0.782;
                case "N": return 0.363;
                case "P": return 0.759;
                case "Q": return 0.376;
                case "R": return 0.167;
                case "S": return 0.466;
                case "T": return 0.542;
                case "V": return 0.854;
                case "W": return 0.898;
                case "Y": return 0.90;
                case "Z": return 0.0;
                case "-": return 1.5;
                default : return 0.0;


            }

        }
        private static void printArray(double array[]){
            for(int i=0 ; i<array.length; i++) {
                if(i<array.length)
                    System.out.print(array[i]+",");
                else
                    System.out.print(array[i]);

            }
            System.out.println();
        }

}
