import java.util.ArrayList;
import java.util.List;

/**
 * Created by carolina on 15/05/2017.
 */
public class Testes {

    public static void main(String[] args) throws Exception {
        Gauss gauss1 = new Gauss(0.5, 2, 10.02594478941936, -10.02594478941936 , 0.1421076870773704,0.4643269806024326);
        Gauss gauss2 = new Gauss(0.25,3,  12.20597251194456, -12.20597251194456, 0.2974943986762133,1.8424072051479376);
       //Gauss gauss3 = new Gauss(0.25, 1.3, 33.42180010720883, -33.42180010720883, 0.3322288883170215,0.49111355583020927);
        //Gauss gauss4 = new Gauss(0.25, 3.1, 33.14903854754591, -33.14903854754591, 0.8852021961619718,1.2682026622298648);
        List<Gauss> listagauss = new ArrayList<>();
        listagauss.add(gauss1);
        listagauss.add(gauss2);
        //listagauss.add(gauss3);
        //listagauss.add(gauss4);



        Mix novamix = new Mix(listagauss.size(),listagauss);

        Amostra amostra = ReadAmostra.read("../EM20.csv");

        long startTime = System.currentTimeMillis();

        Mix melhorado = Melhoramento.melhoraTudo(amostra, novamix);

        long endTime = System.currentTimeMillis();

        float duration = (endTime - startTime) / 1000f;

        System.out.println(melhorado);
        System.out.println(duration);

    }
}
