package uiowa.hhaim.correlation;

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

    private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\New Project\\AllClades_JustSequences\\Glyco Sites_All Clades\\Results\\TSV Files\\B_NA_Chronic.txt";
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
            ChiSquareTest testStatistic = new ChiSquareTest();
            PrintWriter writer =  new PrintWriter("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\New Project\\AllClades_JustSequences\\Glyco Sites_All Clades\\Results\\output2.txt");
            for(int i=8; i<13;i++){
                xList = excel.excelColumns.get(Integer.toString( i ));
                xArray = new double[xList.size()];
                for(int k=0; k< xList.size();k++) {
                    xArray[k] = Double.parseDouble( xList.get( k ) );
                }

                //For chi-square test
                long xDummy[] = new long[xArray.length];
                for(int k=0; k< xArray.length;k++) {
                    if(xArray[k] == 0){
                        xDummy[k] = 0;//(long)xArray[k];
                    }
                    else{
                        xDummy[k] = 1;
                    }
                }
                for(int j=i+1; j<13;j++){
                    //System.out.print("("+i+","+j+") ");
                    writer.append("("+i+";"+j+"),");
                    yList = excel.excelColumns.get(Integer.toString( j ));
                    yArray = new double[yList.size()];
                    //to keep elements into the array in order to use spearman correlation
                    for(int k=0; k< yList.size();k++){
                        yArray[k] = Double.parseDouble( yList.get(k) );
                    }
                    //for chi-square test
                    double yDummy[] = new double[yArray.length];
                    for(int k=0; k< yArray.length;k++) {
                        if(yArray[k] == 0){
                            yDummy[k] = 0;//yArray[k];
                        }
                        else{
                            yDummy[k] = 1;
                        }
                    }

                    double nonZeroChange = 0.0;
                    double allChanges = 0.0;

                    //For nonZeroChanges and ZeroChanges
                    for(int l=0; l< xArray.length; l++){
                        if(xArray[l]!=0){
                            if(yArray[l] !=0 ){
                                allChanges++;
                                nonZeroChange++;
                            }
                            else{
                                allChanges++;
                            }
                        }
                        else{
                            if(yArray[1] != 0 ){
                                     allChanges++;
                             }
                        }
                    }

                    double corr = Spearman.getCorrelation( xArray,yArray );
                    //System.out.print(corr+" "+Spearman.getPvalue(corr, xArray.length));
                    writer.append(corr+","+Spearman.getPvalue(corr, xArray.length)+",");

                    //Proportion, all Changes
                    writer.append("("+(nonZeroChange/allChanges)+";"+allChanges+"),");

                    double noOfOneOne = nonZeroChange;
                    double noOfZeroZero = xArray.length - allChanges;
                    double noOfZeroOneAndOneZero = allChanges - nonZeroChange;
                    double all = xArray.length;
                    //Option #1
                    writer.append( ","+ ((noOfOneOne/noOfZeroOneAndOneZero)*(1-(noOfZeroZero/all)) )+"");

                    //option #2
                    double chiSquare = testStatistic.chiSquare(yDummy, xDummy);
                    double chiSquareP = testStatistic.chiSquareTest( yDummy, xDummy );
                    //Option #2a
                    writer.append(","+ (chiSquare*(noOfOneOne/all))+"");
                    //Option #2b
                    writer.append(","+ (chiSquareP*(noOfOneOne/all))+"");

                    //Option #3
                    double rs = eliminateZeroZero_SpearmanCorrelation(xArray,yArray,allChanges);
                    writer.append(","+ (rs*(1-(noOfZeroZero/all)))+" ");


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

    public static double eliminateZeroZero_SpearmanCorrelation(double[] x, double[] y, double allChanges){
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
        return Spearman.getCorrelation( xArray,yArray );

    }
}
