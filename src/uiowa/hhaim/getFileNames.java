package uiowa.hhaim;
import java.io.*;

/**
 * Created by kandula on 9/22/2017.
 */
public class getFileNames {

    public static void main(String args[]){
        File folder = new File("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Volatility_Data\\V3 Loop Volatility\\For Volatility- Myside\\Distances\\USCC");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.print(listOfFiles[i].getName().replace( ".fas","" )+",");
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }
}
