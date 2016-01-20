package lib;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Alexis Espinoza on 11/11/15.
 */
public class Reader {
    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }
    public static int runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        // System.out.println(command + " exitValue() " + pro.exitValue());
        return pro.exitValue();
    }
    public static String outPut(String command)throws Exception{
        Process pro = Runtime.getRuntime().exec(command);
        BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        boolean band=false;
        while (((line = br.readLine()) != null)&&(!band)) {
            sb.append(line).append("\n");
            band=true;
        }
        pro.waitFor();
        String numberReturn="1";
        if(band) {
            String[] numberOfItems = sb.toString().split(" ");
            int numberOfItemsInt=Integer.parseInt(numberOfItems[1]);
            numberOfItemsInt=numberOfItemsInt+1;
            numberReturn=String.valueOf(numberOfItemsInt);
        }
        return numberReturn;
    }
}
