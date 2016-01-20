import lib.Generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author  Alexis Espinoza
 *
 */
public class CmdLineParser {

    /**
     * Main function only determines which operation is being used, and passes the rest of the information to individual functions
     * to handle the defined operations.
     *
     * @param args String-based arguments for this parser to build a Hadoop File on.
     * @throws IllegalArgumentException Exception is thrown if first argument does not match the one of the supported operations
     */
    public static void main(String[] args) {
        switch (args[0]){
            case "Count":
                if(args.length==3)
                    generateCountHadoopFile(args);
                else
                    generateCountByColumnHadoopFile(args);
                break;
            case "TopX":
                generateTopXHadoopFile(args);
                //NOTE: Break statement not used to force a drop into the default case until this operation is implemented
                break;
            case "Search":
                generateSearchHadoopFile(args);
                //NOTE: Break statement not used to force a drop into the default case until this operation is implemented
                break;
            default:
                throw new IllegalArgumentException("Operation "+args[0]+" is not supported in this version.");
        }

    }

    /**
     * First supported operation. Can be used to generate a Hadoop file that will get a count of each value in a defined column
     *
     * @param args String array of arguments passed into main function
     */
    private static void generateCountHadoopFile(String[] args) {
        if(args.length != 3){
           /* throw new IllegalArgumentException("Improper number of arguments. "
                    + "Calls should follow the form: Count DATASET <name> COLUMN <number>");*/
            throw new IllegalArgumentException("Improper number of arguments. "
                    + "Calls should follow the form: Count DATASET <name>");
        }
       // boolean argsGood = checkDatasetAndColumn(args);
        boolean argsGood=true;
        if (argsGood){
            //Use StringBuilders to read in text files and append in Count code where needed
            StringBuilder header = parseFile("src/hadoopFiles/count/Hadoop1.txt");
            StringBuilder middle = parseFile("src/hadoopFiles/count/Hadoop2.txt");
            StringBuilder end = parseFile("src/hadoopFiles/count/Hadoop3.txt");
            StringBuilder map = new StringBuilder();
            StringBuilder reduce = new StringBuilder();

            map.append("\t\tString line = value.toString();\n");//.split(\",\");\n");
            map.append("\t\tStringTokenizer tokenizer = new StringTokenizer(line);\n");
            map.append("\t\twhile (tokenizer.hasMoreTokens()) {\n");
            map.append("\t\t\tword.set(tokenizer.nextToken());\n");
            map.append("\t\t\tcontext.write(word, one);\n");

            map.append("\t\t}\n");

            reduce.append("\t\tint sum = 0;\n");
            reduce.append("\t\tfor (IntWritable val : values) {\n");
            reduce.append("\t\t\tsum += val.get();\n");
            reduce.append("\t\t}\n");
            reduce.append("\t\tcontext.write(key, new IntWritable(sum));\n");

            StringBuilder totalFile = new StringBuilder(header.toString()+map.toString()+middle.toString()+reduce.toString()+end.toString());
            String userDirectory = System.getProperty("user.dir");
            File directory = new File(userDirectory);
            String fileName = "AutoMR.java";
            //String fileName = "count.java";
            writeToFile(totalFile, directory, fileName, args[2]);
        }
    }
    private static void generateCountByColumnHadoopFile(String[] args) {
        if(args.length != 5){
            throw new IllegalArgumentException("Improper number of arguments. "
                    + "Calls should follow the form: Count DATASET <name> COLUMN <number>");

        }
        boolean argsGood = checkDatasetAndColumn(args);

        if (argsGood){
            //Use StringBuilders to read in text files and append in Count code where needed
            StringBuilder header = parseFile("src/hadoopFiles/count/Hadoop1.txt");
            StringBuilder middle = parseFile("src/hadoopFiles/count/Hadoop2.txt");
            StringBuilder end = parseFile("src/hadoopFiles/count/Hadoop3.txt");
            StringBuilder map = new StringBuilder();
            StringBuilder reduce = new StringBuilder();

            map.append("\t\tString[] lineFields = value.toString().split(\",\");\n");//.split(\",\");\n");
            //map.append("\t\tText outputKey = new Text(lineFields["+args[4]+"]);\n");
            map.append("\t\tword.set(new Text(lineFields["+args[4]+"]));\n");
            map.append("\t\t\tcontext.write(word, one);\n");


            reduce.append("\t\tint sum = 0;\n");
            reduce.append("\t\tfor (IntWritable val : values) {\n");
            reduce.append("\t\t\tsum += val.get();\n");
            reduce.append("\t\t}\n");
            reduce.append("\t\tcontext.write(key, new IntWritable(sum));\n");

            StringBuilder totalFile = new StringBuilder(header.toString()+map.toString()+middle.toString()+reduce.toString()+end.toString());
            String userDirectory = System.getProperty("user.dir");
            File directory = new File(userDirectory);
            String fileName = "AutoMR.java";
            //String fileName = "count.java";
            writeToFile(totalFile, directory, fileName, args[2]);
        }
    }
    /**
     * Second supported operation. Can be used to generate a Hadoop file that will get the X highest values
     * and their counts from a given column. If X is higher than the number of possible values in a column, all values
     * will be returned.
     *
     * @param args String array of arguments passed into main function
     */
    private static void generateTopXHadoopFile(String[] args) {
        if(args.length != 6){
            throw new IllegalArgumentException("Improper number of arguments. "
                    + "Calls should follow the form: TopX <integer> DATASET <name> COLUMN <number>");
        }
        boolean argsGood = checkDatasetAndColumn(args);
        try {
            int xValue = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("X value is not a number. Please use an integer.");
            return;
        }
        if (argsGood){
            //Use StringBuilders to read in text files and append in Count code where needed
            StringBuilder header = parseFile("src/hadoopFiles/topX/Hadoop1.txt");
            StringBuilder middle = parseFile("src/hadoopFiles/topX/ReduceForTopX.txt");
            StringBuilder cleanUp = parseFile("src/hadoopFiles/topX/cleanup.txt");
            StringBuilder topNCombiner = parseFile("src/hadoopFiles/topX/topNCombiner.txt");
            StringBuilder sort = parseFile("src/hadoopFiles/topX/sort.txt");
            StringBuilder end = parseFile("src/hadoopFiles/topX/Hadoop3.txt");


            StringBuilder map = new StringBuilder();
            StringBuilder reduce = new StringBuilder();
            StringBuilder cleanUpBuilder = new StringBuilder();
            StringBuilder topNCombinerBuilder = new StringBuilder();


            map.append("\t\tString line = value.toString();\n");
            map.append("\t\tint i=0;\n");
            map.append("\t\tStringTokenizer itr = new StringTokenizer(line,\",\");\n");
            map.append("\t\twhile (itr.hasMoreTokens()) {\n");
            map.append("\t\tword.set(itr.nextToken());\n");
            map.append("\t\tif(i=="+args[5]+")\n");
            map.append("\t\tcontext.write(word, one);\n");
            map.append("\t\ti++;\n");
            map.append("\t\t}\n");


            reduce.append("\t\tint interestingLevel=0;\n");
            reduce.append("\t\tfor(IntWritable val : values){\n");
            reduce.append("\t\tinterestingLevel+=val.get();\n");
            reduce.append("\t\t}\n");
            reduce.append("\t\tcountMap.put(new Text(key), new IntWritable(interestingLevel));\n");

            cleanUpBuilder.append("\t\tMap<Text, IntWritable> sortedMap = sortByValues(countMap);\n");
            cleanUpBuilder.append("\t\tint counter = 0;\n");
            cleanUpBuilder.append("\t\tfor (Text key: sortedMap.keySet()) {\n");
            cleanUpBuilder.append("\t\tif (counter ++ == "+args[1]+") {\n");
            cleanUpBuilder.append("\t\tbreak;\n");
            cleanUpBuilder.append("\t\t}\n");
            cleanUpBuilder.append("\t\tcontext.write(key, sortedMap.get(key));\n");
            cleanUpBuilder.append("\t\t}\n");

            topNCombinerBuilder.append("\t\tint sum = 0;\n");
            topNCombinerBuilder.append("\t\tfor (IntWritable val : values) {\n");
            topNCombinerBuilder.append("\t\tsum += val.get();\n");
            topNCombinerBuilder.append("\t\t}\n");
            topNCombinerBuilder.append("\t\tcontext.write(key, new IntWritable(sum));\n");

            StringBuilder totalFile = new StringBuilder(header.toString()+map.toString()+middle.toString()+reduce.toString()+cleanUp.toString()+cleanUpBuilder.toString()+topNCombiner.toString()+sort.toString()+end.toString());
            String userDirectory = System.getProperty("user.dir");
            File directory = new File(userDirectory);
            String fileName = "AutoMR.java";
            //String fileName = "topx.java";
            writeToFile(totalFile, directory, fileName,args[3]);
        }
    }

    /**
     * Third supported operation. Can be used to generate a Hadoop file that will get all data instances with a given value in
     * the predefined column. If the value does not exist, this function will return a statement saying "No instances found",
     * or something to that effect.
     *
     * Data should hopefully have identifying feature in the first column, as that will be returned
     *
     * @param args String array of arguments passed into main function
     */
    private static void generateSearchHadoopFile(String[] args) {
        if(args.length != 6){
            throw new IllegalArgumentException("Improper number of arguments. "
                    + "Calls should follow the form: Search <value> DATASET <name> COLUMN <number>");
        }
        boolean argsGood = checkDatasetAndColumn(args);
        if (argsGood){
            //Use StringBuilders to read in text files and append in Count code where needed
            StringBuilder header = parseFile("src/hadoopFiles/count/Hadoop1.txt");
            StringBuilder middle = parseFile("src/hadoopFiles/count/Hadoop2.txt");
            StringBuilder end = parseFile("src/hadoopFiles/count/Hadoop3.txt");
            StringBuilder map = new StringBuilder();
            StringBuilder reduce = new StringBuilder();
            String value = "\"" + args[1] + "\"";
            map.append("\t\tString[] values = value.toString().split(\",\");\n");
            map.append("\t\tString comparisonVal = values["+args[5]+"];\n");
            map.append("\t\tif(comparisonVal.compareTo("+value+") == 0){\n");
            map.append("\t\t\tword.set(values[0]);\n");
            //map.append("\t\t\tone.set(values["+args[5]+"]);\n");
            //map.append("\t\t\toutput.collect(word,one);\n");
            map.append("\t\t\tword.set(values["+args[5]+"]);\n");
            map.append("\t\t\tcontext.write(word, one);\n");
            map.append("\t\t}\n");

            reduce.append("\t\tint sum = 0;\n");
            reduce.append("\t\tfor (IntWritable val : values) {\n");
            reduce.append("\t\t\tsum += val.get();\n");
            reduce.append("\t\t}\n");
            reduce.append("\t\tcontext.write(key, new IntWritable(sum));\n");

            StringBuilder totalFile = new StringBuilder(header.toString()+map.toString()+middle.toString()+reduce.toString()+end.toString());
            String userDirectory = System.getProperty("user.dir");
            File directory = new File(userDirectory);
            //String fileName = "search.java";
            String fileName = "AutoMR.java";
            writeToFile(totalFile, directory, fileName,args[3]);
        }
    }

    /**
     * This function will take a StringBuilder object and write out to the given file in the output directory
     * @param period StringBuilder object to write out
     * @param outputDir Directory to put file in
     * @param fileName Name of file to write out
     */
    private static void writeToFile(StringBuilder period, File outputDir, String fileName, String dataSet) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputDir.getAbsolutePath()+"/src/org/myorg/"+fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED TO CREATE FILEWRITER");
            return;
        }
        try {
            fw.write(period.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("FAILED TO WRITE TO FILE "+fileName);
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("WARNING: FAILED TO CLOSE FILEWRITER. JAVA MAY OVERFLOW");
        }
        String[] parts= fileName.split("\\.");

        try {
            Generator.compileJarHadoop(parts[0], dataSet);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Takes the name of a file, reads it in, and converts it into a useable Stringbuilder object
     * @param string Name of the file to read in
     * @return StringBuilder object containing the contents of the file
     */
    private static StringBuilder parseFile(String string) {
        File f = new File(string);
        System.out.println("File is located at:"+f.getAbsolutePath());
        BufferedReader bf = null;
        StringBuilder toReturn = new StringBuilder();

        try {
            bf = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("FILE MISSING! UNABLE TO COMPLETE!");
            return null;
        }

        try {
            String line = bf.readLine();
            while(line != null){
                toReturn.append(line+"\n");
                line = bf.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("READ FAILED");
        }
        try {
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED TO CLOSE BUFFERED READER");
        }
        return toReturn;
    }

    /**
     * This function makes sure that the last 4 arguments are all correct
     * @param args Arguments passed into main function
     * @return True if all arguments are good, false otherwise
     *
     */
    private static boolean checkDatasetAndColumn(String[] args) {
        int len = args.length;
        String dataset = args[len-4];
        //String datasetName = args[len-3];
        //Consider removing if HDFS access does not work
        //removed temporarily until moved over to HDFS system
        String column = args[len-2];
        String colNumStr = args[len-1];
        int colNum = 0;

        if(dataset.compareTo("DATASET") != 0){
            throw new IllegalArgumentException("Improper argument arrangement. "
                    + "Calls should follow the form: Count DATASET <name> COLUMN <number>");
        }
        if(column.compareTo("COLUMN") != 0){
            throw new IllegalArgumentException("Improper argument arrangement. "
                    + "Calls should follow the form: Count DATASET <name> COLUMN <number>");
        }
        //TODO Uncomment if HDFS access works
        //TODO fix HDFS filepath
		/*File f = new File("hdfs:input/"+datasetName);
		if(!f.exists()){
			throw new IllegalArgumentException("Invalid Dataset name. Dataset "+datasetName+" not found. "
					+ "Calls should follow the form: Count DATASET <name> COLUMN <number>");
		}*/
        try {
            colNum = Integer.parseInt(colNumStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column number was not an integer. "
                    + "Calls should follow the form: Count DATASET <name> COLUMN <number>");
        }

        return true;
    }
}