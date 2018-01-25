package uiowa.hhaim.correlation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kandula on 1/25/2018.
 */

class Excel {
    Map<String, ArrayList<String>> excelColumns;

    public Excel() {
        excelColumns = new LinkedHashMap<String, ArrayList<String>>();
    }
}
public class CalculateRandPValues {
    private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\New Project\\AllClades_JustSequences\\Glyco Sites_All Clades\\Results\\TSV Files\\IC_Chronic.txt";
    public static void main(String args[]){
        BufferedReader br = null;
        FileReader fr = null;
        try {
            String sCurrentLine;

            br = new BufferedReader( new FileReader( Datafile ) );
            ArrayList<String[]> result = new ArrayList<>();
            Excel excel = new Excel();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }
            ArrayList<String> positions = new ArrayList<>();
            String temp[] = result.get(0);
            String prev = "";
            int positionFlag = 0;
            for (int i = 0; i < result.get( 0 ).length; i++) {
                if(temp[i].equals( "-" ) || temp[i].equals( "" )){
                    positions.add(prev+"-"+Integer.toString(positionFlag+1));
                    excel.excelColumns.put((prev+"-"+Integer.toString(positionFlag+1)),new ArrayList<>(  ));
                    positionFlag++;
                }
                else{
                    positions.add(temp[i]);
                    prev = temp[i];
                    excel.excelColumns.put(prev,new ArrayList<>(  ));
                    positionFlag = 0;
                }
            }
            result.remove( 0 );
            ArrayList<String> columnTemp;
            for(String[] data: result){
                for(int i=0; i<data.length;i++){
                    columnTemp = excel.excelColumns.get(positions.get(i));
                    columnTemp.add(data[i]);
                    excel.excelColumns.put(positions.get(i),columnTemp);
                }
            }

            double[] xArray=null,yArray=null;
            ArrayList<String> xList = null, yList = null;
            PrintWriter writer =  new PrintWriter("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\New Project\\AllClades_JustSequences\\Glyco Sites_All Clades\\Results\\output2.txt");
            for(int i=1; i<856;i++){
                xList = excel.excelColumns.get(Integer.toString( i ));
                xArray = new double[xList.size()];
                for(int k=0; k< xList.size();k++) {
                    xArray[k] = Double.parseDouble( xList.get( k ) );
                }
                for(int j=i+1; j<857;j++){
                    //System.out.print("("+i+","+j+") ");
                    writer.append("("+i+";"+j+"),");
                    yList = excel.excelColumns.get(Integer.toString( j ));
                    yArray = new double[yList.size()];
                    //to keep elements into the array in order to use spearman correlation
                    for(int k=0; k< yList.size();k++){
                        yArray[k] = Double.parseDouble( yList.get(k) );
                    }
                    double corr = Spearman.getCorrelation( xArray,yArray );
                    //System.out.print(corr+" "+Spearman.getPvalue(corr, xArray.length));
                    writer.append(corr+","+Spearman.getPvalue(corr, xArray.length));
                    //System.out.println();
                    writer.append("\n");
                }
            }
            System.out.println("Hello");
            writer.close();

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
}
