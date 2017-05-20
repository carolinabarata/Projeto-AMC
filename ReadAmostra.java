import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class ReadAmostra {

    public static Amostra read(String filePath) throws Exception {
        File file = new File(filePath);
        BufferedReader CSVFile = new BufferedReader(new FileReader(file));
        Amostra result = new Amostra();

        String dataRow = CSVFile.readLine(); // Ignora a primeira linha porque tem o nome das colunas
        while ((dataRow = CSVFile.readLine()) != null) {
            String[] lineArr = dataRow.split(";");

            // such functional much map wow
            double[] lineInts = Arrays.stream(lineArr).mapToDouble(Double::parseDouble).toArray();
            result.add((int) lineInts[0], lineInts[1], lineInts[2]);

        }

        CSVFile.close();
        return result;
    }

}