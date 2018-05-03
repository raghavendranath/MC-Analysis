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
