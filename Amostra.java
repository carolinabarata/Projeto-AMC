import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta classe foi implementada com um HashMap de listas de pontos (lista da classe Ponto),
 * em que cada elemento do HashMap respresenta um índice, ou seja uma pessoa.
 * Cada lista de pontos correspondentes a um índice representam assim as medições dessa pessoa
 */
public class Amostra {
    Map<Integer, List<Ponto>> amostra;


    /**
     * O construtor cria uma amostra vazia.
     */
    public Amostra() {
        this.amostra = new HashMap<>();
    }

    /**
     * Este método recebe os valores de uma medição (índice, tempo e valor)
     * e adiciona este elemento à amostra.
     * A inserção do novo elemento é feita de acordo com o índice. É adicionada à lista de pontos do índice respetivo
     */
    public void add(int i, double t, double v) {

        List<Ponto> pessoai = amostra.get(i);
        if (pessoai == null) {
            pessoai = new ArrayList<>();
            amostra.put(i, pessoai);
        }
        pessoai.add(new Ponto(t, v));
    }

    /**
     * Este método devolve o comprimento da Amostra.
     */
    public int length() {
        int sum = 0;
        for (List<Ponto> pessoa : amostra.values()) {
            sum = sum + pessoa.size();
        }
        return sum;
    }

    /**
     * Este método recebe um índice, e devolve uma lista de pontos, correspondentes a esse índice.
     */
    public List<Ponto> indice(int i) {
        return amostra.get(i);
    }

    /**
     * Este método calcula o número de índices diferentes existentes na amostra, ou seja,
     * o número de pessoas (K).
     */
    public int calK() {
        return amostra.size();
    }



    /**
     * Este método recebe uma posição, e devolve um array com a medição correspondente a essa posição
     * (índice, tempo e valor). É considerado que a primeira posição da Amostra a posição 0.
     */
    //assumindo que a 1a posição é a zero
    public double[] element(int posicao) {
        List<double[]> lista = new ArrayList<>();
        for (Map.Entry<Integer, List<Ponto>> entry : amostra.entrySet()) {
            for(Ponto ponto : entry.getValue()){
                double[] vetor = {entry.getKey(), ponto.t, ponto.y};
                lista.add(vetor);
            }
        }
        return lista.get(posicao);
    }

    /**
     * Este método recebe uma Amostra other e adiciona esta Amostra à já existente.
     */
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

