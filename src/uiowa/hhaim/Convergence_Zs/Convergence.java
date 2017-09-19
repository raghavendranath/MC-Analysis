package uiowa.hhaim.Convergence_Zs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


class Patient{
    String ID;
    ArrayList<TimePoint> tp;
    static class TimePoint{
        String ID; // timepoint
        String day; // actual day
        HashMap<String, Double> aminoAcids;
        double total; // for the count of Amino acids in a timepoints
        TimePoint(String id){
            this.ID = id;
            aminoAcids = new HashMap<>(  );
            //adding all amino acids to the hashmap
            char ch = 'A';
            for(int i=0; i<26;i++){
                if(ch != 'B' && ch!= 'J' && ch!= 'O' && ch!='U' && ch!= 'X') {
                    aminoAcids.put( Character.toString( ch ), 0d );
                }
                ch++;
            }
            //adding the newly added Z types
            aminoAcids.put("Z1",0d);
            aminoAcids.put("Z2",0d);
            aminoAcids.put("Z3",0d);

            aminoAcidsLogodds = new HashMap<>( );
            aminoAcidLogoddsComparison = new HashMap<>(  );
            total =0;
        }
        HashMap<String, Double> aminoAcidsLogodds;

        //for comparing proportion with the logodds of that proportion. (Point no:1 date: 9/11/2017
        HashMap<String,Double[]> aminoAcidLogoddsComparison;
    }
    Patient(String name){
        this.ID = name;
        this.tp = new ArrayList<>(  );

    }
    boolean contains(String dayTP){
        if(tp.isEmpty())
            return false;
        for(TimePoint time:tp ){
            if(dayTP.equals( time.ID ))
                return true;
        }
        return false;
    }
    int getTimePointIndex(String dayTP){
        for(int i=0; i< tp.size(); i++){
            if(dayTP.equals( tp.get(i).ID ))
                return i;
        }
        return -1;
    }
}
public class Convergence {
    private static final String sheet = "U:\\ResearchData\\rdss_hhaim\\LAB PROJECTS\\Raghav\\Analysis\\Convergence\\ProgramData\\c_339.txt";
    final static private double v = 6;
    public static void main(String args[]) {
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //fr = new FileReader(Datafile);
            //br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader( new FileReader( sheet ) );
            ArrayList<String[]> result1 = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                result1.add( sCurrentLine.trim().split( "\t" ) );
            }
            result1.remove(0);
            ArrayList<String[]> result = new ArrayList<>();
            //Working on different formats of PNGS sites i.e "Z"
            for(String[] data: result1){
                String  aa = "NULL"; //Amino acid after PNGS
                if(data.length == 6 ){
                    if(!data[3].equals("N"))
                        aa = data[3];
                    else{
                        if(data[4].equals("P"))
                            aa = data[3];
                        else{
                            if(data[5].equals("T"))
                                aa = "Z";
                            else{
                                if(data[5].equals("S")){
                                    switch(data[4]){
                                        case "L": aa = "Z1";
                                                  break;
                                        case "V": aa = "Z2";
                                                  break;
                                        case "I": aa = "Z3";
                                                  break;
                                        default:  aa = "Z";
                                                  break;
                                    }
                                }
                                else
                                    aa = data[3];
                            }
                        }
                    }
                    result.add(new String[]{data[0], data[1], data[2], aa});
                }


            }


/*
            for(String[] data: result){
                 System.out.println(data[0]+","+data[1]+","+data[2]+","+data[3]);
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
*/

            ArrayList<Patient> patient = new ArrayList<>();
            ArrayList<String> previous_patients = new ArrayList<>();
            for(String[] data: result) {
                if (!previous_patients.contains( data[0] )) {
                    patient.add( new Patient( data[0] ) );
                    previous_patients.add( data[0] );
                }
                int pat_index = previous_patients.indexOf( data[0] );
                if (pat_index > patient.size())
                    break;
                Patient patient_temp = patient.get( pat_index );
                if(!patient_temp.contains( data[2] )) {
                    patient_temp.tp.add( new Patient.TimePoint(data[2]));
                }
                int tp_index = patient_temp.getTimePointIndex( data[2] );
                if(tp_index == -1)
                    break;
                Patient.TimePoint tp_temp = patient_temp.tp.get(tp_index);
/*                 if(!tp_temp.aminoAcids.containsKey( data[3] ))
                    tp_temp.aminoAcids.put(data[3],0d);*/
                tp_temp.aminoAcids.put(data[3], tp_temp.aminoAcids.get(data[3])+1);
                tp_temp.total++;
                tp_temp.day = data[1];
            }

            //The below loop combines the results of Z
            for(Patient pat:patient){
                for(Patient.TimePoint time: pat.tp){
                    Set set = time.aminoAcids.entrySet();
                    Iterator i = set.iterator();
                    double value = 0.0d;

                    //To check if any of the Z types are present or not. If not we will not add Z to the hashmap
                    boolean flag = false;
                    while(i.hasNext()){
                        Map.Entry element = (Map.Entry)i.next();
                        String aa = element.getKey().toString();
                        switch(aa){
                            case "Z1": value = value+Double.parseDouble(element.getValue().toString())*0.43;
                                       flag = true;
                                       break;
                            case "Z2": value = value+Double.parseDouble(element.getValue().toString())*0.79;
                                       flag = true;
                                       break;
                            case "Z3": value = value+Double.parseDouble(element.getValue().toString())*0.86;
                                       flag = true;
                                       break;
                        }
                    }
                    time.aminoAcids.remove("Z1");
                    time.aminoAcids.remove("Z2");
                    time.aminoAcids.remove("Z3");
                    /*if(flag){
                        if(time.aminoAcids.containsKey( "Z" ))
                            time.aminoAcids.put("Z",time.aminoAcids.get("Z")+value);
                        else
                            time.aminoAcids.put("Z",value);
                    }*/
                    time.aminoAcids.put("Z",time.aminoAcids.get("Z")+value);

                }
            }




            for(Patient pat: patient){
                for(Patient.TimePoint time: pat.tp){
                    Set set =time.aminoAcids.entrySet();
                    Iterator i = set.iterator();
                    while(i.hasNext()){
                        Map.Entry element = (Map.Entry)i.next();
                        double value = Double.parseDouble( element.getValue().toString());
                        double prob = value/time.total;
                        //double probMin = 0.01d/time.total;
                        double probBar = (time.total - value)/time.total;
                        time.aminoAcidsLogodds.put((String)element.getKey(),Math.log10(((Math.exp(v*(prob-1))+0.3)/1.3)/((Math.exp(v*(probBar-1))+0.3)/1.3)));
                        //For comparison between the proportion and the formula
                        time.aminoAcidLogoddsComparison.put((String)element.getKey(),new Double[]{prob,((Math.exp(v*(prob-1))+0.3)/1.3)});

/*                        //This is for breakpoint to check how the value changes for Z
                        if(element.getKey().equals("Z"))
                            System.out.println("I am Z");*/
                    }
                }
            }

            String aminoAcidNeeded = "H"; //Change this depending on the amino acid you want

            for(Patient pat: patient){
                int size = pat.tp.size();
                for(int i=0; i<size-1;i++){
                    //Covering the point number 1 of discussion 9/14/17. Eliminating all non participants
                    double value1 = pat.tp.get(i).aminoAcids.get(aminoAcidNeeded);
                    double value2 = pat.tp.get(i+1).aminoAcids.get( aminoAcidNeeded );
                    if(value1 != 0d || value2 != 0d){
                       //n_t1 is n at time point t+1
                        double n_t1 = 1/(1+Math.exp(-0.3*(pat.tp.get(i+1).total - 6)));

                        //n_t is n at time point t
                        double n_t = 1/(1+Math.exp(-0.3*(pat.tp.get(i).total - 6)));
                        //deltaT is the time diffeerence
                        double deltaT = ( Double.parseDouble(pat.tp.get(i+1).day) - Double.parseDouble(pat.tp.get(i).day) );
                        double dT = 1/(1+Math.exp(-0.2*(Math.sqrt(deltaT) - 20)));

                        //Value at timepoint T
                        double value_T = pat.tp.get(i).aminoAcidsLogodds.get(aminoAcidNeeded);

                        //Value at timepoint T+1
                        double value_T1 = pat.tp.get(i+1).aminoAcidsLogodds.get(aminoAcidNeeded);

                        //Bias
                        double biasDiff = value_T1 - value_T;

                        //Bias with sigmoid of n+1
                        double bias_n1 = biasDiff * dT * n_t1;

                        //Bias with sigmoid of n+1 and n
                        double bias_n1_n = bias_n1*n_t;

                        System.out.println(pat.ID+","+(i+2)+","+(i+1)+","+pat.tp.get(i+1).total+","+pat.tp.get(i).total+","+n_t1+","+n_t+","+dT+","+value_T1+","+value_T+","+biasDiff+","+bias_n1+","+bias_n1_n);


                    }
                    }

            }

/*            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            for(Patient pat: patient){
                int size = pat.tp.size();
                for(int i=0; i<size;i++){

                    System.out.println(pat.ID+","+(i+1)+","+pat.tp.get(i).aminoAcidLogoddsComparison.get(aminoAcidNeeded)[0]+","+pat.tp.get(i).aminoAcidLogoddsComparison.get(aminoAcidNeeded)[1]);
                }

            }
*/

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
