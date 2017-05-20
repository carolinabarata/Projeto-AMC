
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
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

    //TODO: passar este método para a gaussiana
    public static BigDecimal probj(Amostra amostra, Gauss thetaj) {
        int K = amostra.calK2();
        BigDecimal prod = BigDecimal.ONE;
        for(int i = 0; i < K; i++) {
            List<Ponto> pontosi = amostra.indice(i);
            prod = prod.multiply(probCond(pontosi, thetaj));
        }
        return prod;
    }

    /**
     * Método auxiliar do método prob
     */
    public static BigDecimal probCond(List<Ponto> pontos, Gauss thetaj) {
        double sum = 0;
        for (Ponto ponto : pontos) {
            sum += sumAux(ponto, thetaj);
        }
        BigDecimal expoente = new BigDecimal(-1 / (2 * Math.pow(thetaj.sig, 2)) * sum);
        BigDecimal exponencial = BigDecimalMath.exp(expoente);
        BigDecimal den = new BigDecimal(1 / (Math.pow(2 * Math.PI * Math.pow(thetaj.sig, 2), pontos.size() / 2d)));
        return den.multiply(exponencial);
    }

    public static double logProbj(Amostra amostra, Gauss thetaj){
        int K = amostra.calK2();
        double sum = 0;
        for(int i = 0 ; i < K; i++ ){
            List<Ponto> pontosi = amostra.indice(i);
            double expoente = auxExpoente(pontosi, thetaj);
            sum = sum + Math.log(1 / (Math.pow(2 * Math.PI * Math.pow(thetaj.sig, 2), pontosi.size() / 2d))) + expoente;
        }
        return sum;
    }

    public static double auxExpoente(List<Ponto> pontos, Gauss thetaj) {

        double sum = 0;

        for (Ponto ponto : pontos) {
            sum += sumAux(ponto, thetaj);
        }
        return -1 / (2 * Math.pow(thetaj.sig, 2)) * sum;
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