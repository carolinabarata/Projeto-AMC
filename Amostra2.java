import java.util.ArrayList;
import java.util.List;

/**
 * Created by carolina on 19/05/2017.
 */
public class Amostra2 {
    ArrayList<List<Ponto>> amostra;
    List<Ponto> pessoa;




    public void add(int i, double t, double v){
        Ponto ponto = new Ponto(t,v);
        List<Ponto> pessoai = amostra.get(i);
        pessoai.add(new Ponto(t,v));
    }

    public int length(){
        int sum = 0;
        for(List<Ponto> pessoa : amostra ){
            sum = sum + pessoa.size();
        }
        return sum;
    }

    public List<Ponto> indice(int i){
        return amostra.get(i);
    }

    //public Amostra2 join(Amostra other){

    //}


}

