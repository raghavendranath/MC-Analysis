package uiowa.hhaim.covolatility;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by kandula on 5/3/2018.
 */
public class Covolatility_RandPValue_Matrix {

    //Change the below file location to the tab separated file you are using - Alexa
    //private static final String Datafile = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Env Volatility Forecasting Project\\EVF MS Data and Analyses\\EVF Data and Analyses after PNGS Conversion\\EVF MS ANALYSES\\TSV Files\\B_NA_Chronic - Testing.txt";
    private static final String Datafile = "C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\B_NA_Complete_Chronic.txt";
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


            //Initialize a matrix to store all data.
            //Alexa - Please change the values as below
            String[][] matrixRs = new String[positions.size()][positions.size()];
            String[][] matrixPvalue = new String[positions.size()][positions.size()];

            //Change the i value range - Alexa. Change the initialization value of i to the position you are using
            // Change the condition value to n-1. N is the last position in your file
            for(int i=0; i<positions.size()-1;i++){
                xList = excel.excelColumns.get(positions.get(i));
                xArray = new double[xList.size()];
                for(int k=0; k< xList.size();k++) {
                    xArray[k] = Double.parseDouble( xList.get( k ) );
                }

                //change the condition here - Alexa
                //Change the condition to N. N is the last position in your file
                for(int j=i+1; j<positions.size();j++){
                    yList = excel.excelColumns.get(positions.get(j));
                    yArray = new double[yList.size()];
                    //to keep elements into the array in order to use spearman correlation
                    for(int k=0; k< yList.size();k++){
                        yArray[k] = Double.parseDouble( yList.get(k) );
                    }


                    double corr = Spearman.getCorrelation( xArray,yArray );
                    //System.out.print(corr+" "+Spearman.getPvalue(corr, xArray.length));
                    matrixRs[i][j] = Double.toString(corr);
                    matrixPvalue[i][j] = Double.toString(Spearman.getPvalue(corr, xArray.length));

                }
            }


            //Change the location of the output file for writer1 and writer2 - Alexa
            //PrintWriter writer =  new PrintWriter("U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Env Volatility Forecasting Project\\EVF MS Data and Analyses\\EVF Data and Analyses after PNGS Conversion\\EVF MS ANALYSES\\output2.txt");
            PrintWriter writer1 =  new PrintWriter("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\Output\\covolatility_Rs.txt");
            PrintWriter writer2 =  new PrintWriter("C:\\Users\\kandula.HEALTHCARE\\Desktop\\Temp\\Output\\covolatility_pValues.txt");

            writer1.append( "rs_values\t" );
            writer2.append( "p_values\t" );

            for(String pos: positions){
                writer1.append( pos+"\t" );
                writer2.append( pos+"\t" );
            }
            writer1.append("\n");
            writer2.append("\n");
            for(int i=0; i< matrixRs.length; i++){
                writer1.append((i+1)+"\t");
                writer2.append((i+1)+"\t");
                for(int m=0; m<=i;m++){
                    writer1.append(" \t");
                    writer2.append(" \t");
                }
                for(int j=i+1; j<matrixRs[0].length; j++){
                    writer1.append(matrixRs[i][j]+"\t");
                    writer2.append(matrixPvalue[i][j]+"\t");
                }
                writer1.append("\n");
                writer2.append("\n");
            }


            System.out.println("Hello");
            writer1.close();
            writer2.close();


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
