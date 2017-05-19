import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by carolina on 19/05/2017.
 */
public class Runner {
    public Amostra amostra;
    public Mix theta;

    public void runAndSave(File file) throws IOException {
        System.out.println(file.getAbsolutePath());
        System.out.println(amostra);
        FileWriter fw = new FileWriter(file);
        fw.write(amostra.toString());
        fw.flush();
        fw.close();
        System.out.println("Wrote");
    }
}
