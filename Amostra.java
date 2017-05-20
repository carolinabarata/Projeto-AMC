import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by carolina on 19/05/2017.
 */
public class Amostra {
    Map<Integer, List<Ponto>> amostra;


    /**
     * O construtor cria uma amostra vazia.
     */
    public Amostra() {
        this.amostra = new HashMap<>();
    }


    public void add(int i, double t, double v) {

        List<Ponto> pessoai = amostra.get(i);
        if (pessoai == null) {
            pessoai = new ArrayList<>();
            amostra.put(i, pessoai);
        }
        pessoai.add(new Ponto(t, v));
    }

    public int length() {
        int sum = 0;
        for (List<Ponto> pessoa : amostra.values()) {
            sum = sum + pessoa.size();
        }
        return sum;
    }

    public List<Ponto> indice(int i) {
        return amostra.get(i);
    }

    public int calK() {
        return amostra.size();
    }

    //assumindo que a 1a posição é a zero
    public Ponto element(int posicao) {
        List<Ponto> lista = new ArrayList<>();
        for (List<Ponto> pessoa : amostra.values()) {
            lista.addAll(pessoa);
        }
        return lista.get(posicao);
    }

    public void join(Amostra other){
        for(int indice = 0; indice < other.calK(); indice++){
            List<Ponto> pontosOutro = other.indice(indice);
            List<Ponto> pessoai = amostra.get(indice);
            if (pessoai == null) {
                pessoai = new ArrayList<>();
                amostra.put(indice, pessoai);
            }

            pessoai.addAll(pontosOutro);

        }
    }

}

