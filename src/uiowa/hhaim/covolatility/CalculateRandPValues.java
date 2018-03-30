package uiowa.hhaim.covolatility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveExceptionTest;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
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

    //Change the below file location to the tab separated file you are using - Alexa
    //private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Env Volatility Forecasting Project\\EVF MS Data and Analyses\\EVF Data and Analyses after PNGS Conversion\\EVF MS ANALYSES\\TSV Files\\B_NA_Chronic - Testing.txt";
    private static final String Datafile = "C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\B_Allregions.txt";
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
            //To calculate the proportion of nonZero changes with all change combinations


            ArrayList<String> xList = null, yList = null;
            //For ChiSquare Test
            //ChiSquareTest testStatistic = new ChiSquareTest();
            //Change the location of the output file - Alexa
            //PrintWriter writer =  new PrintWriter("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Env Volatility Forecasting Project\\EVF MS Data and Analyses\\EVF Data and Analyses after PNGS Conversion\\EVF MS ANALYSES\\output2.txt");
            PrintWriter writer =  new PrintWriter("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\output.txt");
            //Change the i value range - Alexa. Change the initialization value of i to the position you are using
            // Change the condition value to n-1. N is the last position in your file
            for(int i=0; i<positions.size()-1;i++){
                xList = excel.excelColumns.get(positions.get(i));
                xArray = new double[xList.size()];
                for(int k=0; k< xList.size();k++) {
                    xArray[k] = Double.parseDouble( xList.get( k ) );
                }

                //For chi-square test
               /* long xDummy[] = new long[xArray.length];
                for(int k=0; k< xArray.length;k++) {
                    if(xArray[k] != 0){
                        xDummy[k] = 1;//(long)xArray[k];
                    }
                    else{
                        xDummy[k] = 1;
                    }
                }*/
                //change the condition here - Alexa
                //Change the condition to N. N is the last position in your file
                for(int j=i+1; j<positions.size();j++){
                    //System.out.print("("+i+","+j+") ");
                    writer.append("("+positions.get(i)+";"+positions.get(j)+"),");
                    yList = excel.excelColumns.get(positions.get(j));
                    yArray = new double[yList.size()];
                    //to keep elements into the array in order to use spearman correlation
                    for(int k=0; k< yList.size();k++){
                        yArray[k] = Double.parseDouble( yList.get(k) );
                    }
                    //for chi-square test
                    /*double yDummy[] = new double[yArray.length];
                    for(int k=0; k< yArray.length;k++) {
                        if(yArray[k] == 0){
                            yDummy[k] = 0;//yArray[k];
                        }
                        else{
                            yDummy[k] = 1;
                        }
                    }*/

                    double nonZeroChange = 0.0;
                    double allChanges = 0.0;
                    double noOfZeroOnes = 0.0;
                    double noOfOneZeros = 0.0;

                    //For nonZeroChanges and ZeroChanges
                    for(int l=0; l< xArray.length; l++){
                        if(xArray[l]!=0){
                            if(yArray[l] !=0 ){
                                allChanges++;
                                nonZeroChange++;
                                continue;
                            }
                            else{
                                allChanges++;
                                noOfOneZeros++;
                                continue;
                            }
                        }
                        else{
                            if(yArray[l] != 0 ){
                                     allChanges++;
                                     noOfZeroOnes++;
                                     continue;
                             }
                        }
                    }

                    //System.out.println("("+i+","+j+")   :"+allChanges+","+nonZeroChange);

                    double corr = Spearman.getCorrelation( xArray,yArray );
                    //System.out.print(corr+" "+Spearman.getPvalue(corr, xArray.length));
                    writer.append(corr+","+Spearman.getPvalue(corr, xArray.length));

                    //Proportion, all Changes
                    //writer.append("("+(nonZeroChange/allChanges)+";"+allChanges+"),");

                    double noOfOneOne = nonZeroChange;
                    double noOfZeroZero = xArray.length - allChanges;
                    double noOfZeroOneAndOneZero = allChanges - nonZeroChange;
                    double all = xArray.length;


                    writer.append(","+noOfOneOne+","+noOfOneZeros+","+noOfZeroOnes+","+noOfZeroOneAndOneZero+","+noOfZeroZero);
                    //Option #1
                    if(noOfZeroZero == all){
                        writer.append(","+0+"");
                    }
                    else{
                        writer.append( ","+ ((noOfOneOne/(noOfZeroOneAndOneZero+0.25))*(1-(noOfZeroZero/all)) )+"");
                   }

                    //option #2
                    /*double chiSquare = testStatistic.chiSquare(yDummy, xDummy);
                    double chiSquareP = testStatistic.chiSquareTest( yDummy, xDummy );
                    double chi[] = eliminateZeroZero_ChiSquare( xArray, yArray, allChanges);
                    if(chi == null){
                        System.out.println("Bad request!");
                        System.exit( 0 );
                    }
                    double chiSquare = chi[0];
                    double chiSquareP = chi[1];
                    //Option #2a
                    /*writer.append(","+ (chiSquare*(noOfOneOne/all))+"");
                    //Option #2b
                    writer.append(","+ ((1/chiSquareP)*(noOfOneOne/all))+"");*/

                    //Option #3
                    /*double Prs = eliminateZeroZero_SpearmanCorrelationPValue(xArray,yArray,allChanges);
                    writer.append(","+ (1/Prs)*(1-(noOfZeroZero/all))+" ");*/


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

    public static double eliminateZeroZero_SpearmanCorrelationPValue(double[] x, double[] y, double allChanges){
        double[] xArray = new double[(int)allChanges];
        double[] yArray = new double[(int)allChanges];
        for(int i=0,k=0; i<x.length && k<(int)allChanges ;i++){
            if(x[i] != 0){
                xArray[k] = x[i];
                yArray[k] = y[i];
                k++;
            }
            else{
                if(y[i] != 0){
                    xArray[k] = x[i];
                    yArray[k] = y[i];
                    k++;
                }
            }
        }
        /*System.out.println("++++++++++++++++++++++");
        System.out.println(allChanges);

        for(int i=0; i<xArray.length;i++){
            System.out.println(xArray[i]+","+yArray[i]);
        }*/
        double corr = Spearman.getCorrelation( xArray,yArray );
        return Spearman.getPvalue(corr, xArray.length);

    }

    public static double[] eliminateZeroZero_ChiSquare(double[] x, double[] y, double allChanges) {
        long[] xArray = new long[(int) allChanges];
        long[] yArray = new long[(int) allChanges];
        for (int i = 0, k = 0; i < x.length && k < (int) allChanges; i++) {
            if (x[i] != 0) {
                xArray[k] = 1;
                if (y[i] != 0) {
                    yArray[k] = 1;
                } else {
                    yArray[k] = 0;
                }
                k++;
            } else {
                if (y[i] != 0) {
                    xArray[k] = 0;
                    yArray[k] = 1;
                    k++;
                }
            }

        }


        /*for(int i=0; i<xArray.length;i++){
            System.out.println(xArray[i]+","+yArray[i]);
        }*/
        long[][] counts = {xArray, yArray};
       /* for(int i=0; i<counts.length;i++){
            for(int j=0; j<counts[i].length;j++){
                System.out.print(counts[i][j]+",");
            }
            System.out.println();
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");*/
        //For ChiSquare Test
        ChiSquareTest testStatistic = new ChiSquareTest();
        double[] result = new double[2];
        result[0] = testStatistic.chiSquare(counts );
        result[1] = testStatistic.chiSquareTest( counts );


        //This is for testing
        /*long[] observed1 = {10, 12, 12, 10, 15};
        long[] observed2 = {15, 10, 10, 15, 5};
        long[][] count1 = {observed1, observed2};
        System.out.println(testStatistic.chiSquare( count1 ));
        System.out.println(testStatistic.chiSquareTest( count1 ));*/
        return result;
    }


   //ChiSquare without removing the (0,0) ones
   /*public static double[] eliminateZeroZero_ChiSquare(double[] x, double[] y){
       long[] xArray = new long[x.length];
       long[] yArray = new long[y.length];
       if(x.length == y.length){
           for(int i=0; i<x.length;i++){
               if(x[i] != 0){
                   xArray[i] = 1;
               }
               else{
                   xArray[i] = 0;
               }
           }

           for(int i=0; i<y.length;i++){
               if(y[i] != 0){
                   yArray[i] = 1;
               }
               else{
                   yArray[i] = 0;
               }
           }
           //Copying both arrays to counts
           long[][] counts = {xArray, yArray};
           ChiSquareTest testStatistic = new ChiSquareTest();
           double[] result = new double[2];
           result[0] = testStatistic.chiSquare(counts );
           result[1] = testStatistic.chiSquareTest( counts );
           return result;

       }
       return null;
   }*/

}
