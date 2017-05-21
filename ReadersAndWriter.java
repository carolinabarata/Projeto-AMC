import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadersAndWriter {

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

    //ordem dos parâmetros de um theta: w, sig, a1, a2, b1, b2
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