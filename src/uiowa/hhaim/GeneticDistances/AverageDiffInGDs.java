package uiowa.hhaim.GeneticDistances;

import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kandula on 3/30/2018.
 */
public class AverageDiffInGDs {
    //file names
    static ArrayList<String> files;
    static ArrayList<FileDetails>  arr= new ArrayList<>();
    public static void main(String args[]){
        if(args.length != 2){
            System.out.println("Just give folder location and output file in the arguments");
            System.exit(0);
        }
        String location = args[0].replace("\\","\\\\");
        File folder = new File(location);
        ReadFiles.listFilesForFolder(folder);
        files = new ArrayList<>(ReadFiles.files);
        for(String file: files){
            FileReader fr = null;
            BufferedReader br = null;
            try{
                FileDetails newFile = new FileDetails( new File(location+"\\\\"+file), location+"\\\\"+file, file.replace(".fas","") );
                fr = new FileReader(newFile.location);

                br = new BufferedReader(fr);
                String sCurrentLine;
                ArrayList<String[]> buffer = new ArrayList<>();
                while ((sCurrentLine = br.readLine()) != null) {
                    buffer.add(sCurrentLine.trim().split("\t"));
                }
                System.out.println(file);

                buffer.remove(0);
                for(String data[]: buffer){
                    if(data.length > 2)
                        newFile.distances.add(Double.parseDouble(data[2]));
                }

                arr.add(newFile);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{

                try {

                    if (br != null)
                        br.close();

                    if (fr != null)
                        fr.close();


                } catch (Exception ex) {

                    ex.printStackTrace();

                }
            }
        }

        try{
            PrintWriter writer = new PrintWriter(args[1].replace("\\","\\\\"));
            for(int i=0; i < arr.size(); i++){
                for(int j=i+1; j<arr.size(); j++){
                    FileDetails firstFile = arr.get(i);
                    FileDetails secondFile = arr.get(j);
                    double mean = 0.0;
                    for(int m = 0; m< firstFile.distances.size(); m++){
                        for(int n=0; n< secondFile.distances.size(); n++){
                            mean += Math.abs(firstFile.distances.get(m) - secondFile.distances.get(n));
                        }
                    }
                    mean = mean / (double)(firstFile.distances.size()*secondFile.distances.size());
                    writer.append("("+firstFile.name+", "+secondFile.name+") - "+mean+"\n");
                }
            }
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
}

class ReadFiles{
    static ArrayList<String> files = new ArrayList<>();
    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry.getName());
            }
        }
    }
}


class FileDetails{
    File name;
    ArrayList<Double> distances;
    String location;
    String patientName;
    public FileDetails(File name, String location, String patientName){
        if(name.exists()) {
            this.name = name;
            distances = new ArrayList<>();
            this.location = location;
            this.patientName = patientName;
        }
        else{
            System.out.println("File doesnot exist");
            System.exit(0);
        }
    }
}