
import java.util.HashMap;
import java.util.List;

/**
 * A classe Mix foi implementada como uma lista (List da biblioteca do Java) constituída por elementos da classe Gauss.
 * Esta classe representa então a lista de parâmetros (theta) constituída por vários thetaj.
 */
public class Mix {
    /**
     * theta representa a lista de gaussianas.
     */
    List<Gauss> theta;
    /**
     * M representa o tamanho da Mix, ou seja, o número de gaussianas/thetaj(Gauss).
     */
    int M;

    /**
     * O construtor desta classe recebe o número de parâmetros (thetaj) M, e uma lista de parâmetros theta.
     */
    public Mix(int M, List<Gauss> theta) {
        this.theta = theta;
        this.M = M;
    }


    @Override
    public String toString() {
        StringBuilder thetas = new StringBuilder();

        for (Gauss gauss: theta) {
            thetas.append(" ").append(gauss.toString()).append(" \n");
        }
        return "Mix{" +
                "thetas=" + thetas +
                ", M=" + M +
                '}';
    }

    //TODO verificar se isto funciona
    public double prob(Amostra amostra) {
        int K = amostra.calK();
        double sum = 0;
        for (Gauss thetaj : theta) {
            double prod = 1;
            for(int i = 0; i < K; i++) {
                List<Ponto> pontosi = amostra.indice(i);
                prod = prod * probj(pontosi, thetaj);
            }
            sum += thetaj.w * prod;
        }

        return sum;

    }

    /**
     * Este método recebe uma lista de pontos (lista de elementos da classe Ponto),
     * que representam os dados de um paciente ao longo do tempo,
     * e determina a probabilidade dessa lista de pontos ser observada pela mistura(theta).
     */

    /**
     * Método auxiliar do método prob
     */
    public static double f(Gauss thetaj, double t) {
        return thetaj.a1 * Math.exp(-thetaj.b1 * t) +
                thetaj.a2 * Math.exp(-thetaj.b2 * t);
    }

    /**
     * Método auxiliar do método prob
     */
    private static double sumAux(Ponto ponto, Gauss thetaj) {
        return Math.pow(ponto.y - f(thetaj, ponto.t), 2);
    }

    /**
     * Método auxiliar do método prob
     */
    public static double probj(List<Ponto> pontos, Gauss thetaj) {
        double sum = 0;
        for (Ponto ponto : pontos) {
            sum += sumAux(ponto, thetaj);
        }
        double exp = Math.exp(-1 / (2 * Math.pow(thetaj.sig, 2)) * sum);
        return (1 / (Math.pow(2 * Math.PI * Math.pow(thetaj.sig, 2), pontos.size() / 2d))) * exp;
    }

    /**
     * Este método retorna a lista de parâmetros atual.
     */
    public List<Gauss> theta() {
        return theta;
    }

    /**
     * Este método recebe uma nova lista de parâmetros theta e substitui a lista atual por esta.
     */
    public void update(List<Gauss> mix) {
        theta = mix;
        M = theta.size();
    }

    public Gauss getThetaJ(int j){
        return theta.get(j - 1);
    }

    public void updateThetaj(int j, Gauss thetaj){
        theta.set(j - 1, thetaj);
    }
}