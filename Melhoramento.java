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
    public static final int MAX_R = 100;
    // probabilidade de aceitação de um bdj
    public static final int PROB_ACEITACAO_INV = 100;
    // valor mínimo do peso de uma gaussiana (w) para esta ser considerada
    public static final double W_MINIMO = 0.005;

    /**
     * Método que vai fazer iteradas dos melhoramentos para cada um dos parâmetros,
     * e para cada uma das gaussianas pertencentes à Mix.
     */
    public static Mix melhoraTudo(Amostra amostra, Mix theta) {
        Mix thetaK = theta;
        //esta variável foi criada para guardar o nº de iterações que se faz,
        // para depois se poder fazer print deste valor
        int it = 0;
        while (true) {
            List<Gauss> thetajList = new ArrayList<>(thetaK.theta());
            Mix thetaKMaisUm = new Mix(thetajList);
            //este boolean usa-se para se conseguir parar o ciclo while(true),
            // quando a condição de convergência do EM já é verificada
            boolean stop = true;
            //é feita mais uma iterada
            it ++;
            //imprime-se o nº da iterada que está a ser feita
            System.out.println("Iteração nº: " + it);

            // Ciclo que percorre cada uma das gaussianas.
            // Melhoram-se todos os parâmetros de uma gaussiana e só depois se passa para a próxima gaussiana.
            for (int j = 1; j <= thetaK.M; j++) {

                //vai-se buscar a gaussiana j da matriz theta resultante da iterada anterior (thetaK).
                //essa gaussiana vai ser agora melhorada na iterada k+1, daí o nome thetajKMaisUm
                Gauss thetajKMaisUm = thetaKMaisUm.getThetaJ(j);

                //Melhora-se o wj e atualiza-se este valor na gaussiana j
                thetajKMaisUm = thetajKMaisUm.changew(melhorawj(amostra, thetaKMaisUm, thetaKMaisUm.getThetaJ(j)));

                //Atualiza-se a Mix com a nova gaussiana atualizada
                thetaKMaisUm.updateThetaj(j, thetajKMaisUm);

                //Se o parâmetro w for muito pequeno, assumimos que pode ser desprezado,
                //e deixam de ser atualizados os outros parâmetros para esta gaussiana,
                // enquanto este valor for desprezável. O valor de W_MINIMO é um atributo da classe Melhoramento
                // e portanto pode ser alterado no início da classe se assim se pretender.
                if(thetajKMaisUm.w <= W_MINIMO){
                    //é feito um print a avisar que está a ser ignorada uma gaussiana,
                    //e indica-se que gaussiana é, e qual o valor de wj respetivo
                    System.out.println("A ignorar thetaj: " + j + " (w = " + thetajKMaisUm.w + ")");
                    continue;
                }

                //Melhora-se o aj e guarda-se o valor absoluto desse aj melhorado
                double ajmelhorado = Math.abs(melhoraaj(amostra, thetaKMaisUm, thetaKMaisUm.getThetaJ(j)));
                //atualiza-se o a1 na gaussiana j com o valor de aj melhorado (a1j é positivo)
                thetajKMaisUm = thetajKMaisUm.changea1(ajmelhorado);
                //atualiza-se o a2 na gaussiana j com o valor de -aj melhorado (a2j é negativo)
                thetajKMaisUm = thetajKMaisUm.changea2(-ajmelhorado);

                //Atualiza-se a Mix com a nova gaussiana atualizada
                thetaKMaisUm.updateThetaj(j, thetajKMaisUm);

                //Melhora-se os parâmetros bdj e este método devolve theta já atualizado com estes novos valores
                thetaKMaisUm = melhorabdj(amostra, thetaKMaisUm, j);

                thetajKMaisUm = thetaKMaisUm.getThetaJ(j);

                //Melhora-se o sigmaj e atualiza-se este valor na gaussiana j
                thetajKMaisUm = thetajKMaisUm.changesigma(melhorasigmaj(amostra, thetaK, thetaK.getThetaJ(j), thetajKMaisUm));

                //Atualiza-se a Mix com a nova gaussiana atualizada
                thetaKMaisUm.updateThetaj(j, thetajKMaisUm);

                //Calculam-se os delta b1 e b1,
                // que correspondem aos quadrados da diferença entre bd da iterada k+1 e bd da iterada k, para d = 1, 2
                double deltaB1 = Math.pow(thetajKMaisUm.b1 - thetaK.getThetaJ(j).b1, 2);
                double deltaB2 = Math.pow(thetajKMaisUm.b2 - thetaK.getThetaJ(j).b2, 2);

                //se as condições de convergência do EM se verificarem, o boolean true continua a true,
                //senão, muda para false.
                stop = stop &&
                        deltaB1 < 0.00001 &&
                        deltaB2 < 0.00001;
            }
            //quando se chega à última gaussiana, só se este boolen estiver a true, ou seja,
            //se para todas as guassianas estas condições se verificaram, é que se sai do while(true), e se retorna
            // a mix que corresponde à mistura final atualizada
            if (stop) {
                return thetaKMaisUm;
            }
            //se para alguma das gaussianas estas condições não se verificaram, ou seja, se o boolean stop está false,
            //volta a ser feita uma nova iterada
            else {
                thetaK = thetaKMaisUm;
            }
        }
    }

    /**
     * Método que vai melhorar o parâmetro wj.
     */
    public static double melhorawj(Amostra pessoas, Mix theta, Gauss thetaj) {
        int K = pessoas.calK();

        double soma = 0;
        for (int i = 0; i < K; i++) {
            List<Ponto> pontos = pessoas.indice(i);
            double r = xij(pontos, theta.M, thetaj, theta);
            soma = soma + r;
        }
        return (1d / K) * soma;
    }
    /**
     * Método auxiliar utilizado no melhoramento de vários parâmetros.
     * Dentro deste método é utilizada a classe BigDecimal que permite obter números com uma grande precisão,
     * impedindo assim que valores muito pequenos sejam assumidos como zero pelo Java.
     */
    public static double xij(List<Ponto> pontos, int M, Gauss thetaj, Mix theta) {
        BigDecimal num = new BigDecimal(thetaj.w).multiply(Mix.probCond(pontos, thetaj));
        BigDecimal den = BigDecimal.ZERO;
        for (int u = 1; u <= M; u++) {
            Gauss thetau = theta.theta().get(u - 1);
            den = den.add(new BigDecimal(thetau.w).multiply(Mix.probCond(pontos, thetau)));
        }
        return num.divide(den, RoundingMode.HALF_DOWN).doubleValue();
    }

    /**
     * Método que vai melhorar o parâmetro aj.
     */
    public static double melhoraaj(Amostra pessoas, Mix theta, Gauss thetaj) {
        int K = pessoas.calK();
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

    /**
     * Método que vai melhorar o parâmetro sigmaj.
     */
    public static double melhorasigmaj(Amostra pessoas, Mix theta, Gauss thetajK, Gauss thetajKMaisUm) {
        int K = pessoas.calK();
        double num = 0;
        double den = 0;
        for (int i = 0; i < K; i++) {
            List<Ponto> pontosi = pessoas.indice(i);
            for (int l = 1; l <= pontosi.size(); l++) {
                num = num + xij(pontosi, theta.M, thetajK, theta) *
                        Math.pow(pontosi.get(l - 1).y - Mix.f(thetajKMaisUm, pontosi.get(l - 1).t), 2);
            }
        }
        for (int i = 0; i < K; i++) {
            List<Ponto> pontosi = pessoas.indice(i);
            int n = pontosi.size();
            den = den + n * xij(pontosi, theta.M, thetajK, theta);
        }
        return Math.sqrt(num / den);
    }
    /**
     * Método que vai melhorar o parâmetro bj. Faz iterações (aquecimento/arrefecimento) para cada j
     */
    private static Mix melhorabdj(Amostra amostra, Mix thetaAtual, int j) {
        // primeira iterada é feita com o valor antigo de b (o da iterada k anterior)
        ArrefecimentoResult maxResultTetha = new ArrefecimentoResult(thetaAtual, thetaAtual.logProbj(amostra, thetaAtual.getThetaJ(j)));

        Mix thetaAquecido = thetaAtual; // na primeira iteração não há aquecimento

        //ciclo que vai fazer os R-1 arrefecimentos/aquecimentos (o primeiro já foi feito em cima,
        //em que para o primeiro arrefecimento se ultilizou não um theta resultante de um aquecimento,
        //mas sim o theta da iterada anterior
        for (int R = 1; R < MAX_R; R++) {
            ArrefecimentoResult newTetha = arrefecimento(thetaAquecido, j, amostra);

            //se logprobj do novo theta for maior do que o logprobj do theta que até agora era o máximo
            // (para o qual o probj era máximo) e se são verificadas as condições para os b's,
            //então o novo máximo passa a ser o novo theta.
            if (newTetha.logprobj > maxResultTetha.logprobj &&
                    verificaCondicoes(newTetha.theta, j)) {
                maxResultTetha = newTetha;
            }

            //Faz-se o processo de aquecimento para os dois b's (b1 e b2)
            thetaAquecido = aquecimento(thetaAtual, j, true);
            thetaAquecido = aquecimento(thetaAquecido, j, false);
        }

        //No fim dos R aquecimentos/arrefecimento, devolve-se o theta que corresponde à probabilidade máxima
        return maxResultTetha.theta;
    }

    /**
     * Método auxiliar que faz o processo de aquecimento. O método é usado tanto para fazer o aquecimento
     * do parâmetro b1 como do b2. A única diferença é o boolean isForB1 que é dado como argumento que
     * indica qual dos parâmetros se quer aquecer.
     */
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
        return new Mix(thetajList);
    }

    /**
     * Classe criada para podermos devolver o theta resultante de um processo de arrefecimento
     * e respetiva logprobj para a gaussiana que está a ser melhorada no momento
     */
    static class ArrefecimentoResult {
        Mix theta;
        double logprobj;

        public ArrefecimentoResult(Mix theta, double logprobj) {
            this.theta = theta;
            this.logprobj = logprobj;
        }
    }
    /**
     * Método auxiliar que faz o processo de arrefecimento.
     */
    private static ArrefecimentoResult arrefecimento(Mix theta, int j, Amostra amostra) {

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
            thetajvizinho = thetajatual.changeb1(vizinhob1j);
            thetajvizinho = thetajvizinho.changeb2(vizinhob2j);

            //aqui cria-se um novo theta que tem a gaussiana j substituída por uma nova thetaj
            // que já contém o parâmetro b1 ou b2 vizinho
            List<Gauss> thetajList = new ArrayList<>(thetaMaximo.theta());
            thetajList.set(j - 1, thetajvizinho);
            Mix thetaVizinho = new Mix(thetajList);

            double logprobjVizinho = Mix.logProbj(amostra, thetajvizinho);

            // se a probabilidade do theta que contém o bdj vizinho for maior,
            // atualizam-se valores do maximo.
            // Se não, aceita-se o maximo anterior com uma determinada probabilidade de aceitação,
            //que corresponde ao inverso de PROB_ACEITACAO_INV
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
    /**
     * Método auxiliar que devolve um double random entre a e b (exclusive).
     */
    private static double nrandom(double a, double b) {
        while (true) {
            double random = new Random().nextDouble() * (b - a) + a;
            if (random != a) { // para ter a certeza que o resultado nao inclui o limite inferior
                return random;
            }
        }
    }

    /**
     * Método auxiliar que verifica as condições que relacionam b1 e b2.
     * Neste caso, são verificadas as condições para uma mesma iterada,
     * uma vez que nós melhoramos b1 e b2 ao mesmo tempo
     */
    private static boolean verificaCondicoes(Mix theta, int j) {
        double b1j = theta.getThetaJ(j).b1;
        double b2j = theta.getThetaJ(j).b2;

        return 0 < b1j &&
                b1j < b2j &&
                b2j < 5;
    }

}