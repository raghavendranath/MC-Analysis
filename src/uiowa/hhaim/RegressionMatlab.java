package uiowa.hhaim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kandula on 7/28/2017.
 */
class Patient2{
    String name;
    ArrayList<String> days;
    ArrayList<String> delta_position;
    Patient2(String name){
        this.name = name;
        days = new ArrayList<>();
        delta_position = new ArrayList<>();
    }
}
public class RegressionMatlab {
    private static final String Datafile = "C:\\Users\\kandula.HEALTHCARE\\Desktop\\667.txt";

    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(Datafile);
            br = new BufferedReader(fr);

            String sCurrentLine;
            br = new BufferedReader(new FileReader(Datafile));
            ArrayList<String[]> result = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result.add(sCurrentLine.trim().split("\t"));
            }
            //Mapping each patient
            HashMap<String,Integer> map = new HashMap<>();
            int count= -1;
            for(int i=1; i<result.size()-1; i++){
                if(!map.containsKey(result.get(i)[0])){
                    count++;
                    map.put(result.get(i)[0], count);
                }
            }

            ArrayList<Patient2> patients = new ArrayList<>();
            String previous = "Empty";
            for(int i = 1; i< result.size(); i++){
                String[] data;
                data = result.get(i);
                if(previous.compareTo(data[0]) != 0){
                    patients.add(new Patient2(data[0]));
                    previous = data[0];
                }
                Patient2 patient = patients.get(map.get(data[0]));
                patient.days.add(data[1]);
                patient.delta_position.add(data[2]);
            }

            System.out.println("");
            for(Patient2 patient:patients){
                System.out.print(patient.name+",");

            }
            for(Patient2  patient: patients){
                System.out.print("Pat"+map.get(patient.name)+"X = [");
                for(String day:patient.days){
                    System.out.print(day+" ");
                }
                System.out.println("];");
                System.out.print("Pat"+map.get(patient.name)+"Y = [");
                for(String delta:patient.delta_position){
                    System.out.print(delta+" ");
                }
                System.out.println("];");
            }

/*            for(Patient2  patient: patients) {
                System.out.println("Scatter(Pat" + map.get(patient.name) + "X, Pat" + map.get(patient.name) + "Y);");
                System.out.println("lsline;");
                System.out.println("hold on;");
            }*/
            System.out.println("Slopes = [];");
            for(Patient2 patient: patients){
                System.out.println("dlm"+map.get(patient.name)+" = fitlm(Pat"+ map.get(patient.name) + "X, Pat" + map.get(patient.name) + "Y, 'Intercept', false);");
                System.out.println("Slopes = [Slopes,dlm"+map.get(patient.name)+".Coefficients.Estimate;]");
                System.out.println("plot(dlm"+map.get(patient.name)+");");
                System.out.println("hold on;");
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