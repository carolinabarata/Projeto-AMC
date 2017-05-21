import java.io.File;
import java.io.IOException;

/**
 *Classe que permite correr o melhoramento através dos dados obtidos pelos Readers,
 * e que permite escrever a Mix resultante do melhoramente, utilizando o Writer
 *
 */
public class Runner {
    public Amostra amostra;
    public Mix theta;

    public void runAndSave(File file) throws IOException {

        long startTime = System.currentTimeMillis();

        //corre-se o processo de melhoramente com a amostra e theta
        // obtidos através dos métodos da classe ReadersandWriter
        Mix resultado = Melhoramento.melhoraTudo(amostra, theta);
        //escreve-se o theta resultante deste processo através também dos métodos da classe ReadersandWriter
        ReadersAndWriter.writeMix(resultado, file);

        System.out.println("Terminou");
        long endTime = System.currentTimeMillis();
        float duration = (endTime - startTime) / 1000f;
        //é feito o print da duração do processo de melhoramento
        System.out.println(duration);
    }
}
