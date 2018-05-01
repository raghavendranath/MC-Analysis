package uiowa.hhaim.volatility_persistence;

/**
 * Created by kandula on 5/1/2018.
 * Make sure that format is this way;
 * Patient, ^T, 162Vn, 162Vn-1, 163Vn, 163Vn-1,.. etc
 */
import uiowa.hhaim.covolatility.Spearman.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Excel {
    Map<String, ArrayList<String>> excelColumns;
    Set<String> colNames;

    public Excel() {
        excelColumns = new LinkedHashMap<String, ArrayList<String>>();
        colNames = new LinkedHashSet<>( );
    }
}

public class VolPersist_Analysis {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the file location of volatility Persistence results (Tab delimited).");
        String dataFile = sc.nextLine().trim().replace( "\\","\\\\");
        BufferedReader br = null;
        FileReader fr = null;
        try {
            String sCurrentLine;
            br = new BufferedReader( new FileReader( dataFile ) );
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add( sCurrentLine.trim().split( "\t" ) );
            }
            ArrayList<String> positions = new ArrayList<>();
            String temp[] = result.get(0);
            String prev = "";
            int positionFlag = 0;
            Excel excel = new Excel();
            for (int i = 0; i < result.get( 0 ).length; i++) {
                if(temp[i].equals( "-" ) || temp[i].equals( "" )){
                    positions.add(prev+"-"+Integer.toString(positionFlag+1));
                    excel.excelColumns.put((prev+"-"+Integer.toString(positionFlag+1)),new ArrayList<>(  ));
                    excel.colNames.add(prev+"-"+Integer.toString(positionFlag+1));
                    positionFlag++;
                }
                else{
                    positions.add(temp[i]);
                    prev = temp[i];
                    excel.excelColumns.put(prev,new ArrayList<>(  ));
                    excel.colNames.add(prev);
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

            System.out.println("Computations in progress");



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
