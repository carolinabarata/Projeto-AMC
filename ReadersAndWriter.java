import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadersAndWriter {

    /**
     * Método que permite ler o ficheiro que contém a Amostra.
     * O ficheiro tem de ter a seguinte estrutura:
     * Cada linha contém um valor da amostra que contém, separado por ponto e vírgula e pela ordem referida:
     * índice, tempo e valor
     */
    public static Amostra readAmostra(File file) throws Exception {
        BufferedReader CSVFile = new BufferedReader(new FileReader(file));
        Amostra result = new Amostra();

        CSVFile.readLine(); // Ignora a primeira linha porque tem o nome das colunas

        String dataRow;
        while ((dataRow = CSVFile.readLine()) != null) {
            if(dataRow.isEmpty() ){
                continue;
            }
            String[] lineArray = dataRow.split(";");

            // such functional much map wow
            double[] lineDoubles = Arrays.stream(lineArray).mapToDouble(Double::parseDouble).toArray();
            result.add((int) lineDoubles[0], lineDoubles[1], lineDoubles[2]);

        }

        CSVFile.close();
        return result;
    }
    /**
     * Método que permite ler o ficheiro que contém uma Mix. O ficheiro tem de ter a seguinte estrutura:
     * Cada linha representa os parâmetros de uma gaussiana. Em cada linha, os parâmetros encontram-se seprarados
     * por ponto e vírgula e apresentam a seguinte ordem: w, sigma, a1, a2, b1, b2.
     */
    public static Mix readMix(File file) throws Exception {
        BufferedReader thetaFile = new BufferedReader(new FileReader(file));
        List<Gauss> listaGauss = new ArrayList<>();

        String dataRow;
        while ((dataRow = thetaFile.readLine()) != null) {
            if(dataRow.isEmpty() ){
                continue;
            }
            String[] lineArray = dataRow.split(";");

            // such functional much map wow
            double[] lineDoubles = Arrays.stream(lineArray).mapToDouble(Double::parseDouble).toArray();
            Gauss newGauss = new Gauss(lineDoubles[0], lineDoubles[1],
                    lineDoubles[2], lineDoubles[3], lineDoubles[4], lineDoubles[5]);
            listaGauss.add(newGauss);
        }
        thetaFile.close();
        return new Mix(listaGauss);
    }

    /**
     * Método que permite escrever uma Mix para um ficheiro. O ficheiro vai apresentar a seguinte estrutura:
     * Cada linha representa os parâmetros de uma gaussiana. Em cada linha, os parâmetros encontram-se seprarados
     * por ponto e vírgula e apresentam a seguinte ordem: w, sigma, a1, a2, b1, b2.
     */
    public static void writeMix(Mix mix, File file) throws IOException {
        String mixString = "";
        for (Gauss gauss : mix.theta()) {
            mixString += "" + gauss.w + ";" + gauss.sig + ";" + gauss.a1 + ";" + gauss.a2 + ";" + gauss.b1 + ";" + gauss.b2 + "\n";
        }

        Writer fileWriter = new BufferedWriter(new FileWriter(file));
        fileWriter.write(mixString);
        fileWriter.flush();
        fileWriter.close();
    }

}