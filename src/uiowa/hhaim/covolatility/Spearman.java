package uiowa.hhaim.covolatility;

/**
 * Created by kandula on 1/25/2018.
 * Used: https://www.javatips.net/api/data-algorithms-book-master/src/main/java/org/dataalgorithms/chap23/correlation/Spearman.java
 */

import java.util.List;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelationTest;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
public class Spearman {

    final static SpearmansCorrelation SC = new SpearmansCorrelation();

    public static  double getCorrelation(double[] xArray, double[] yArray) {
        double corr = SC.correlation(xArray, yArray);
        return corr;
    }

    public static  double getPvalue(final double corr, final int n) {
        return getPvalue(corr, (double) n);
    }

    public static  double getPvalue(double corr, double n) {
        double t = Math.abs(corr * Math.sqrt( (n-2.0) / (1.0 - (corr * corr)) ));
        //System.out.println("     t = "+ t);
        TDistribution tdist = new TDistribution(n-2);
        double pvalue = 2.0 * (1.0 - tdist.cumulativeProbability(t));	// p-value worked.
        return pvalue;
    }

    public static  double[] toDoubleArray(List<Double> list) {
        double[] arr = new double[list.size()];
        for (int i=0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}

/*
class Pearson{
    final static ChiSquareTest chi = new ChiSquareTest();
    public static

}*/
