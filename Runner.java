import java.io.File;
import java.io.IOException;

public class Runner {
    public Amostra amostra;
    public Mix theta;

    public void runAndSave(File file) throws IOException {
        Mix resultado = Melhoramento.melhoraTudo(amostra, theta);
        ReadersAndWriter.writeMix(resultado, file);
    }
}
