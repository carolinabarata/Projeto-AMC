import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by carolina on 07/05/2017.
 */
public class Melhoramento {
    // nº de aquecimentos/arrefecimentos que vai ser feito
    public static final int MAX_R = 1000;
    // probabilidade de aceitação de um bdj
    public static final int PROB_ACEITACAO_INV = 100;
    // valor mínimo do peso de uma gaussiana (w) para esta ser considerada
    public static final double W_MINIMO = 0.005;

    public static Mix melhoraTudo(Amostra amostra, Mix theta) {
        Mix thetaK = theta;
        while (true) {
            List<Gauss> thetajList = new ArrayList<>(thetaK.theta());
            Mix thetaKMaisUm = new Mix(thetajList.size(), thetajList);
            boolean stop = true;

            for (int j = 1; j <= thetaK.M; j++) {
                Gauss thetajKMaisUm = thetaKMaisUm.getThetaJ(j);

                thetajKMaisUm = thetajKMaisUm.changew(melhoraw(amostra, thetaKMaisUm, thetaKMaisUm.getThetaJ(j)));
                thetaKMaisUm.updateThetaj(j, thetajKMaisUm); // update thetaK
                if(thetajKMaisUm.w <= W_MINIMO){
                    System.out.println("A ignorar thetaj: " + j + " (w = " + thetajKMaisUm.w + ")");
                    continue;
                }

                double amelhorado = Math.abs(melhoraa(amostra, thetaKMaisUm, thetaKMaisUm.getThetaJ(j)));
                thetajKMaisUm = thetajKMaisUm.changea1(amelhorado);
                thetajKMaisUm = thetajKMaisUm.changea2(-amelhorado);
                thetaKMaisUm.updateThetaj(j, thetajKMaisUm); // update thetaK

                System.out.println("começa b's");
                Gauss thetajMelhorado = melhorabdj(amostra, thetaKMaisUm, j).getThetaJ(j);
                thetajKMaisUm = thetajKMaisUm.changeb1(thetajMelhorado.b1);
                thetajKMaisUm = thetajKMaisUm.changeb2(thetajMelhorado.b2);
                thetaKMaisUm.updateThetaj(j, thetajKMaisUm); // update thetaK

                thetajKMaisUm = thetajKMaisUm.changesigma(melhorasigma(amostra, thetaK, thetaK.getThetaJ(j), thetajKMaisUm));

                thetaKMaisUm.updateThetaj(j, thetajKMaisUm);


                double deltaB1 = Math.pow(thetajKMaisUm.b1 - thetaK.getThetaJ(j).b1, 2);
                double deltaB2 = Math.pow(thetajKMaisUm.b2 - thetaK.getThetaJ(j).b2, 2);

                System.out.println("deltaB1: " + deltaB1);
                System.out.println("deltaB2: " + deltaB2);

                stop = stop && deltaB1 < 0.00001 && deltaB2 < 0.00001;
            }

            if (stop) {
                return thetaKMaisUm;
            } else {
                thetaK = thetaKMaisUm;
            }
        }
    }

    public static double melhoraw(Amostra pessoas, Mix theta, Gauss thetaj) {
        int K = pessoas.calK2();

        double soma = 0;
        for (int i = 0; i < K; i++) {
            List<Ponto> pontos = pessoas.indice(i);
            double r = xij(pontos, theta.M, thetaj, theta);
            soma = soma + r;
        }
        return (1d / K) * soma;
    }

    public static double xij(List<Ponto> pontos, int M, Gauss thetaj, Mix theta) {
        BigDecimal num = new BigDecimal(thetaj.w).multiply(Mix.probCond(pontos, thetaj));
        BigDecimal den = BigDecimal.ZERO;
        for (int u = 1; u <= M; u++) {
            Gauss thetau = theta.theta().get(u - 1);
            den = den.add(new BigDecimal(thetau.w).multiply(Mix.probCond(pontos, thetau)));
        }
        return num.divide(den, RoundingMode.HALF_DOWN).doubleValue();
    }


    public static double melhoraa(Amostra pessoas, Mix theta, Gauss thetaj) {
        int K = pessoas.calK2();
        double num = 0;
        double den = 0;
        for (int i = 0; i < K; i++) {
            List<Ponto> pontosi = pessoas.indice(i);
            for (int l = 1; l <= pontosi.size(); l++) {
                num = num + xij(pontosi, theta.M, thetaj, theta) *
                        pontosi.get(l - 1).y *
                        (Math.exp(-thetaj.b1 * pontosi.get(l - 1).t) - Math.exp(-thetaj.b2 * pontosi.get(l - 1).t));
            }
        }
        for (int i = 0; i < K; i++) {
            List<Ponto> pontosi = pessoas.indice(i);
            for (int l = 1; l <= pontosi.size(); l++) {
                den = den + xij(pontosi, theta.M, thetaj, theta) *
                        Math.pow(Math.exp(-thetaj.b1 * pontosi.get(l - 1).t) - Math.exp(-thetaj.b2 * pontosi.get(l - 1).t), 2);
            }
        }
        return num / den;
    }

    public static double melhorasigma(Amostra pessoas, Mix theta, Gauss thetajK, Gauss thetajKPlus) {
        int K = pessoas.calK2();
        double num = 0;
        double den = 0;
        for (int i = 0; i < K; i++) {
            List<Ponto> pontosi = pessoas.indice(i);
            for (int l = 1; l <= pontosi.size(); l++) {
                num = num + xij(pontosi, theta.M, thetajK, theta) *
                        Math.pow(pontosi.get(l - 1).y - Mix.f(thetajKPlus, pontosi.get(l - 1).t), 2);
            }
        }
        for (int i = 0; i < K; i++) {
            List<Ponto> pontosi = pessoas.indice(i);
            int n = pontosi.size();
            den = den + n * xij(pontosi, theta.M, thetajK, theta);
        }
        return Math.sqrt(num / den);
    }

    // faz iterações (aquecimento/arrefecimento) para cada j
    private static Mix melhorabdj(Amostra amostra, Mix thetaAtual, int j) {
        // primeira iterada é feita com o valor antigo de b (o da iterada k anterior)
        //TODO: alterar isto
        ArrefecimentoResult maxResultTetha = new ArrefecimentoResult(thetaAtual, thetaAtual.logProbj(amostra, thetaAtual.getThetaJ(j)));

        Mix thetaAquecido = thetaAtual; // na primeira iteração não há aquecimento
        for (int R = 1; R < MAX_R; R++) {
            ArrefecimentoResult newTetha = arrefecimento(thetaAquecido, j, amostra);


            if (newTetha.logprobj > maxResultTetha.logprobj &&
                    verificaCondicoes(newTetha.theta, j)) {
                maxResultTetha = newTetha;
            }

            thetaAquecido = aquecimento(thetaAtual, j, true);
            thetaAquecido = aquecimento(thetaAquecido, j, false);

        }

        System.out.println("Probj after cooling (j= " + j + ") " + maxResultTetha.logprobj);

        return maxResultTetha.theta;
    }

    //Método que faz o processo de aquecimento
    private static Mix aquecimento(Mix theta, int j, boolean isForB1) {
        Gauss thetaj = theta.getThetaJ(j);
        Gauss newthetaj;
        if (isForB1) {
            double b1random = nrandom(0, thetaj.b2);
            newthetaj = thetaj.changeb1(b1random);
        } else {
            double b2random = nrandom(thetaj.b1, 5);
            newthetaj = thetaj.changeb2(b2random);
        }

        List<Gauss> thetajList = new ArrayList<>(theta.theta());
        thetajList.set(j - 1, newthetaj);
        return new Mix(thetajList.size(), thetajList);
    }

    //classe criada para podermos devolver o theta resultante de um processo de arrefecimento
    // e respetiva probCond para a gaussiana que está a ser melhorada no momento
    static class ArrefecimentoResult {
        Mix theta;
        double logprobj;

        public ArrefecimentoResult(Mix theta, double logprobj) {
            this.theta = theta;
            this.logprobj = logprobj;
        }
    }

    //Método que faz o processe de arrefecimento
    private static ArrefecimentoResult arrefecimento(Mix theta, int j, Amostra amostra) {
        int iterada = 1;

        double b1jMaximo = theta.getThetaJ(j).b1,
                b2jMaximo = theta.getThetaJ(j).b2;

        Mix thetaMaximo = theta;
        double logprobjMaximo = Mix.logProbj(amostra, thetaMaximo.getThetaJ(j));

        while (true) {

            double vizinhob1j = b1jMaximo + nrandom(-0.01, 0.01);
            double vizinhob2j = b2jMaximo + nrandom(-0.01, 0.01);
            Gauss thetajatual = thetaMaximo.getThetaJ(j);
            Gauss thetajvizinho;

            // aqui copia-se o thetaj atual alterando apenas o bdj respetivo
            // para o código não estar duplicado, para b1 e b2, basta esta parte ser diferente e o resto mantém-se
            thetajvizinho = thetajatual.changeb1(vizinhob1j);
            thetajvizinho = thetajvizinho.changeb2(vizinhob2j);

            //aqui cria-se um novo theta que tem a gaussiana j substituída por uma nova thetaj
            // que já contém o parâmetro b1 ou b2 vizinho
            List<Gauss> thetajList = new ArrayList<>(thetaMaximo.theta());
            thetajList.set(j - 1, thetajvizinho);
            Mix thetaVizinho = new Mix(thetajList.size(), thetajList);

            double logprobjVizinho = Mix.logProbj(amostra, thetajvizinho);

            // se a probabilidade do theta que contém o bdj vizinho for maior,
            // atualizam-se valores do maximo. Se não, aceita-se o maximo anterior com probCond 1/10000
            if (logprobjVizinho > logprobjMaximo) {
                thetaMaximo = thetaVizinho;
                b1jMaximo = vizinhob1j;
                b2jMaximo = vizinhob2j;
                logprobjMaximo = logprobjVizinho;
            } else if (1 == new Random().nextInt(PROB_ACEITACAO_INV)) {
                return new ArrefecimentoResult(thetaMaximo, logprobjMaximo);
            }

        }

    }

    //devolve um double random entre a e b (exclusive)
    private static double nrandom(double a, double b) {
        while (true) {
            double random = new Random().nextDouble() * (b - a) + a;
            if (random != a) { // para ter a certeza que o resultado nao inclui o limite inferior
                return random;
            }
        }
    }

    private static boolean verificaCondicoes(Mix theta, int j) {
        double b1j = theta.getThetaJ(j).b1;
        double b2j = theta.getThetaJ(j).b2;

        return 0 < b1j &&
                b1j < b2j &&
                b2j < 5;
    }

}