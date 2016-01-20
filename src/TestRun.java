/**
 * Created by Alexis Espinoza on 11/11/15.
 */
import lib.Generator;

public class TestRun {
    public static void main(String[] args) {
        try {
            Generator.compileJarHadoop("WordCount", "WordCount.txt");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
