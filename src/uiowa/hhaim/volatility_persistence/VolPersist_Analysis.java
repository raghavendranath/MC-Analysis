package uiowa.hhaim.volatility_persistence;

/**
 * Created by kandula on 5/1/2018.
 * Make sure that format is this way;
 * Patient, ∆T, 162Vn, 162Vn-1, 163Vn, 163Vn-1,.. etc
 * Before using this program, Please sort the entire excel sheet w.r.t ∆T
 */
import uiowa.hhaim.covolatility.Spearman;
import uiowa.hhaim.covolatility.Spearman.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Excel {
    Map<String, ArrayList<String>> excelColumns;
    ArrayList<String> colNames;

    public Excel() {
        excelColumns = new LinkedHashMap<String, ArrayList<String>>();
        colNames = new ArrayList<>( );
    }
}

public class VolPersist_Analysis {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the file location of volatility Persistence results (Tab delimited).");
        String dataFile = sc.nextLine().trim().replace( "\\","\\\\");

        System.out.println("How many bins you are dividing the data ?. (Make sure the bins are properly intervaled)");
        int noOfBins = Integer.parseInt(sc.nextLine().trim());

        System.out.println("Give the day ranges separated by - and press enter for moving on to the next range.");
        System.out.println("For example: 0-30 (Means 0 days to 30 days inclusive");
        int[][] dayRanges = new int[noOfBins][2];

        //DayStart represents start of an interval and DayEnd represents end of the day interval
        int dayStart = 0, dayEnd = 0;

        for(int i=0; i<noOfBins ; i++){
            String[] days = sc.nextLine().trim().split( "-" );
            if(days.length != 2){
                System.out.println("Wrongly inputted. Exiting the program");
                System.exit( 0 );
            }
            dayRanges[dayStart][dayEnd] = Integer.parseInt( days[0] );
            dayRanges[dayStart][dayEnd+1] = Integer.parseInt( days[1] );
            dayStart++;
        }



        sc.close();

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


            //binStart gets the index of the days within the bins.
            //Last bin will go the end of the columns so no need to recording it
            int[] binEndsIndex = new int[noOfBins-1];
            ArrayList<String> deltaTime = excel.excelColumns.get("?T");
            int endOfDayRange = 0;
            for(int i=0; i< deltaTime.size(); i++){

                if( dayRanges[endOfDayRange][1] < Integer.parseInt(deltaTime.get(i)) ){
                    binEndsIndex[endOfDayRange] = i-1;
                    endOfDayRange++;
                }
            }







            //For printing purposes taking a big double array
            double[][] solution = new double[noOfBins*2][(excel.colNames.size()/2)+1];

            //Calculations for getting spearman co-efficient and their p-values

            //Getting two double arrays because the spearman function needs it as input
            double[] xArray,yArray;
            String volAtN = null, volAtN_1 = null;
            System.out.println("The results are: ");
            for(int i=2; i<excel.colNames.size(); i=i+2){
                if(i+1>=excel.colNames.size())
                    break;
                volAtN = excel.colNames.get(i);
                volAtN_1 = excel.colNames.get(i+1);




                xArray = getDoubleArray(excel.excelColumns.get(volAtN));
                yArray = getDoubleArray(excel.excelColumns.get(volAtN_1));


                //Getting the exact position
                volAtN = volAtN.replaceAll("\\D+","");
                volAtN_1 = volAtN_1.replaceAll("\\D+","");

   /*             if(xArray.length == yArray.length && volAtN_1.contains( volAtN )){
                    double corr = Spearman.getCorrelation( xArray,yArray );
                    double pValue =  Spearman.getPvalue(corr, xArray.length);
                    System.out.println(volAtN+","+corr+","+pValue);

                }
                else{
                    System.out.println(excel.colNames.get(i)+", and "+excel.colNames.get(i+1)+" columns are not of same length. Please fix it. Exiting..");
                    System.exit(0);

                }
      */      }

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

    //Gets the double array from string by converting the values to binary 1 or 0.
    // 0 - if volatility value equals to 0.001 and 1 otherwise
    private static double[] getDoubleArray(ArrayList<String> volValues) {
        String[] stringArray = Arrays.copyOf(volValues.toArray(), volValues.size(), String[].class);
        double[] values = new double[stringArray.length];
        int i=0;
        double tempVal = 0.0d;
        for(String str: stringArray){
            tempVal = Double.parseDouble( str );
            if(tempVal == 0.001)
                values[i++]= 0;
            else
                values[i++] = 1;
        }
        return values;


    }
}
