package uiowa.hhaim.volatility_persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kandula on 10/2/2017.
 */
public class AvgandSError {
    private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\volatility persistence\\All Positions\\Serror200.txt";

    public static void main(String args[]){
        BufferedReader br = null;
        FileReader fr = null;

        try {
            String sCurrentLine;
            br = new BufferedReader( new FileReader( sheet ) );
            ArrayList<String[]> data = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                data.add( sCurrentLine.trim().split( "\t" ) );
            }
            String matrix[][] = new String[data.get(0).length+1][data.get(0).length+1];

            HashMap<String, Integer> h = new HashMap<>();
            String volPosition1 = data.get(1)[0];
            if(!h.containsKey(volPosition1))
                h.put(volPosition1,0);
            String[] temp = data.get(0);
            for(int i=0; i< temp.length; i++){
                if(!h.containsKey(temp[i]))
                    h.put(temp[i],i+1);
            }

            data.remove(0);
            for(String elements[]:data){
                int i = h.get(elements[0]);
                for(int j=1;j<elements.length;j++){
                    matrix[i][j] = elements[j];
                }
            }
            int size = matrix[0].length;
            double resultAvg[][] = new double[size/2][size/2];
            double resultDev[][] = new double[size/2][size/2];

            String positions[] = new String[size/2];
            for(int i=0,j=0; i<  size;i=i+2,j++){
                positions[j] = data.get(i)[0];
            }
            for(int i=0; i< positions.length;i++){
                int index = h.get(positions[i]);
                int actualindex = index/2;
                for(int j= 0; j<= actualindex;j++){
                    resultAvg[actualindex][j] = Double.MAX_VALUE;
                    resultDev[actualindex][j] = Double.MAX_VALUE;
                }
                for(int j=index+2; j< size;j=j+2){
                    resultAvg[actualindex][j/2] = (Double.parseDouble(matrix[index][j])+Double.parseDouble(matrix[index+1][j+1]))/2;
                    resultDev[actualindex][j/2] = Math.sqrt( (Math.pow((Double.parseDouble(matrix[index][j])-resultAvg[actualindex][j/2]),2 ) + Math.pow((Double.parseDouble(matrix[index+1][j+1])-resultAvg[actualindex][j/2]),2))/1);
                }
            }
            for(int i=0; i<size/2;i++) {
                for (int j = 0; j < size / 2; j++) {
                    System.out.print( resultAvg[i][j] + "," );
                }
                System.out.println();
            }

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            for(int i=0; i<size/2;i++) {
                for (int j = 0; j < size / 2; j++) {
                    System.out.print( resultDev[i][j] + "," );
                }
                System.out.println();
            }



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
