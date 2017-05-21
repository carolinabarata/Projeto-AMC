import java.io.File;
import java.io.IOException;

public class Runner {
    public Amostra amostra;
    public Mix theta;

    public void runAndSave(File file) throws IOException {

        long startTime = System.currentTimeMillis();

        Mix resultado = Melhoramento.melhoraTudo(amostra, theta);
        ReadersAndWriter.writeMix(resultado, file);

        System.out.println("Terminou");
        long endTime = System.currentTimeMillis();
        float duration = (endTime - startTime) / 1000f;
        System.out.println(duration);
    }
}
