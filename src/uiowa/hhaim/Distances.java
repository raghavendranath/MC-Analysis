package uiowa.hhaim;

import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.*;
import static org.apache.commons.math3.stat.StatUtils.geometricMean;

/**
 * Created by kandula on 8/11/2017.
 */
public class Distances {
    //File folder = new File("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Volatility_Data\\V3 Loop Volatility\\For Volatility- Myside\\Distances\\Maraviroc Distances");
    File folder = new File("U:\\ResearchData\\rdss_hhaim\\DATA\\Primary Data\\HIV-1 ENV NEW SEQUENCES\\Clade B Env\\Genetic Distances\\Genetic distances_NonLongitudinals\\B_USCC_Chronic");

    ArrayList<String> resultedFiles;
    HashMap<String, Double> resultAvg = new HashMap<>();
    HashMap<String, Double> resultGeo = new HashMap<>();
    HashMap<String, Double> resultMax = new HashMap<>();
    public static void main(String args[]){
        Distances d = new Distances();
        ReadFiles.listFilesForFolder(d.folder);
        d.resultedFiles = new ArrayList<>(ReadFiles.files);
        //Collections.sort(d.resultedFiles);
        System.out.println(d.resultedFiles);
        File newFile = null;
        for(String file: d.resultedFiles){
            FileReader fr = null;
            BufferedReader br = null;
            ArrayList<Double> dist = new ArrayList<>();
            try{
                //fr = new FileReader("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Volatility_Data\\V3 Loop Volatility\\For Volatility- Myside\\Distances\\Maraviroc Distances\\"+file);
                fr = new FileReader("U:\\ResearchData\\rdss_hhaim\\DATA\\Primary Data\\HIV-1 ENV NEW SEQUENCES\\Clade B Env\\Genetic Distances\\Genetic distances_NonLongitudinals\\B_USCC_Chronic\\"+file);

                br = new BufferedReader(fr);
                String sCurrentLine;
                ArrayList<String[]> buffer = new ArrayList<>();
                while ((sCurrentLine = br.readLine()) != null) {
                    buffer.add(sCurrentLine.trim().split("\t"));
                }
                System.out.println(file);

                buffer.remove(0);
                for(String data[]: buffer){
                    if(data.length > 2)
                        dist.add(Double.parseDouble(data[2]));
                }
                double data[] = new double[dist.size()];
                for(int i=0; i< dist.size(); i++){
                    data[i] = dist.get(i);
                }
                //System.out.println("++++++++++++++Mean+++++++++++++++");
                if(!d.resultAvg.containsKey(file)){
                    d.resultAvg.put(file,StatUtils.mean(data));
                }
                //System.out.println("++++++++++++++SD+++++++++++++++");
                if(!d.resultGeo.containsKey(file)){
                    d.resultGeo.put(file, StatUtils.geometricMean(data));
                }

                if(!d.resultMax.containsKey(file)){
                    d.resultMax.put(file, StatUtils.max(data));
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{

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
        System.out.println("++++++Averages++++++");
        for (Map.Entry<String, Double> entry : d.resultAvg.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key+","+value);
            // do stuff
        }
        /*System.out.println("++++++GeoMeans++++++");
        for (Map.Entry<String, Double> entry : d.resultGeo.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key+","+value);
            // do stuff
        }*/

        System.out.println("++++++Minimum++++++");
        for (Map.Entry<String, Double> entry : d.resultMax.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key+","+value);
            // do stuff
        }


    }
    public static double geoMean(ArrayList<Double> dist ){
        double product = 1.0;
        for(double d: dist){
            product = product*d;
        }
        return Math.pow(product,1.0/(double)dist.size());
    }
    public double arithMean(ArrayList<Double> dist){
        double sum = 0.0;
        for(double d: dist){
            sum = sum+ d;
        }
        return (sum/dist.size());
    }
}
class ReadFiles{
    static ArrayList<String> files = new ArrayList<>();
    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry.getName());
            }
        }
    }
}
