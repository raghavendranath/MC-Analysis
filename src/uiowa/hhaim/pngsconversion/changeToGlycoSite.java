package uiowa.hhaim.pngsconversion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kandula on 12/20/2017.
 * There will be few spaces at the end (may be after 835 position. Fill them manually
 * Input file is just positions with gaps
 */
 class Excel{
    public Map<String,ArrayList<String>> excelColumns;

    public Excel(){
        excelColumns = new LinkedHashMap<String, ArrayList<String>>(  );
    }

    public ArrayList<String> gycoSite(int position){
        String pos1 = Integer.toString(position);
        String pos2 = Integer.toString(position+1);
        String pos3 = Integer.toString(position+2);
        ArrayList<String> col1, col2, col3;
        String aa1, aa2, aa3;
        ArrayList<String> result = new ArrayList<>(  );
        if(position > 854 && position < 857){
            if(excelColumns.containsKey( pos1 ))
                return excelColumns.get(pos1);
        }
        if(excelColumns.containsKey( pos1 ) && excelColumns.containsKey( pos2 ) && excelColumns.containsKey( pos3 )){
            col1 = excelColumns.get(pos1);
            col2 = excelColumns.get(pos2);
            col3 = excelColumns.get(pos3);
            for(int i=0; (i< col1.size() && i< col2.size() && i< col3.size()) ;i++){
                aa1 = col1.get(i).toLowerCase();
                aa2 = col2.get(i).toLowerCase();
                aa3 = col3.get(i).toLowerCase();
                //System.out.print("Row:"+i+", Position:"+pos1);
                if(aa1.equals( "n" ) && (!aa2.equals("p")) && (aa3.equals( "s" ) || aa3.equals( "t" ))){
                    result.add("Z");
                }
                else{
                    result.add(aa1.toUpperCase());
                }

            }
        }
        return result;
    }
}

public class changeToGlycoSite {
    // private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\excel test files\\rough.txt";
    //private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Env Volatility Forecasting Project\\EVF MS Data and Analyses\\Working Directory\\temp\\C_SA_Chronic.txt";
    //private static final String Datafile = "C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\RAKAI Position Vol.txt";
    final static int MAX = 856;
    public static void main(String args[]){
        BufferedReader br = null;
        FileReader fr = null;
        if(args.length != 2){
            System.out.println("Give [input file location(tsv)] and [outputfile location(.txt)]. Remove spaces in filenames");
            System.exit(1);
        }
        final String Datafile = args[0].replace("\\","\\\\");;
        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);

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


            //int[] features = {295,332,339,386,392,448};
            int[] features = new int[856];
            for(int i=0; i<features.length;i++)
                features[i] = i+1;
            ArrayList<ArrayList<String>> resultSet = new ArrayList<>(  );
            ArrayList<String> resultTemp;
            for(int i=0; i<features.length;i++){
                //System.out.println(i);
                resultTemp =  excel.gycoSite( features[i] );
                resultSet.add(resultTemp);
            }

            //PrintWriter writer =  new PrintWriter("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Env Volatility Forecasting Project\\EVF MS Data and Analyses\\Working Directory\\output2.txt");;
            //PrintWriter writer =  new PrintWriter("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\output2.txt");;
            PrintWriter writer = new PrintWriter(args[1].replace("\\","\\\\"));
            for(int i=0; i< resultSet.get(0).size();i++){
                for(int j=0; j< resultSet.size() && i<resultSet.get(j).size();j++){
                    //System.out.print(resultSet.get(j).get(i)+",");
                    writer.append(resultSet.get(j).get(i)+",");
                }
                //System.out.println();
                writer.append("\n");
            }
            System.out.println("hello");
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
