package lib;

import lib.Reader;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexis Espinoza on 12/1/15.
 */
public class Generator {
    private static String workingDir=System.getProperty("user.dir");
    public static void compileJarHadoop(String javaClass, String dataSet){
        try {
            String outPutFileName ="output"+Reader.outPut("hadoop fs -ls hdfs:/user/hadoop/project3/output/");
            //System.out.println("javac -classpath "+workingDir+"/projectJarFiles/hadoop-0.20.1-core.jar  src/org/myorg/"+javaClass+".java");

            int k =  Reader.runProcess("javac -classpath "+workingDir+"/projectJarFiles/hadoop-0.20.1-core.jar  src/org/myorg/"+javaClass+".java");
            if (k==0) { //jar cvf src/org/myorg/WordCount.jar  src/org/myorg/
                //System.out.println("jar cvf src/org/myorg/"+javaClass+".jar -C src/ .");
                k = Reader.runProcess("jar cvf src/org/myorg/"+javaClass+".jar -C src/ .");
                if(k==0){

                    System.out.println("hadoop jar "+workingDir+"/src/org/myorg/"+javaClass+".jar org.myorg."+javaClass+" /user/hadoop/project3/datasets/"+dataSet+" /user/hadoop/project3/output/"+outPutFileName);
                    Reader.runProcess("hadoop jar "+workingDir+"/src/org/myorg/"+javaClass+".jar org.myorg."+javaClass+" /user/hadoop/project3/datasets/"+dataSet+" /user/hadoop/project3/output/"+outPutFileName);
                    System.out.println("The output has been sucessfully generated in Hadoop /user/hadoop/project3/output/"+outPutFileName);
                    cleanUp(new File(workingDir+"/src/org/myorg/"));

                }else{
                    System.out.println("Unable to create executable JAR");
                }

            }else{
                System.out.println("No class has been provided for compilation");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cleanUp(File file)
            throws IOException {

        String files[] = file.list();
        for (String temp : files) {
            File fileDelete = new File(file, temp);
            fileDelete.delete();
        }
    }

}
