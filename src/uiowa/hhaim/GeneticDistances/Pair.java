package uiowa.hhaim.GeneticDistances;

import java.util.ArrayList;

public class Pair{
    String env1;
    String env2;
    ArrayList<Double> distances;
    double average;
    Pair(String env1, String env2){
        this.env1 = env1;
        this.env2 = env2;
        distances = new ArrayList<>(  );
        average = 0.0;
    }

    void calculateMean(){
        for(int i=0; i< distances.size(); i++){
            average += distances.get(i);
        }
        average =  average/(double)distances.size();
    }

    static int getIndexOfPair(ArrayList<Pair> pairs, Pair newPair){
        for(int i=0; i<pairs.size(); i++){
            Pair temp = pairs.get(i);
            if(temp.env1.equals( newPair.env1 ) && temp.env2.equals( newPair.env2 ))
                return i;
        }
        return -1;
    }

}
