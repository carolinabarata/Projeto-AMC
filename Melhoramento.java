import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by carolina on 07/05/2017.
 */
public class Melhoramento {

    public static Mix melhoraTudo(Amostra amostra, Mix theta) {
        Mix thetaK = theta;
        while (true) {
            List<Gauss> thetajList = new ArrayList<>(thetaK.theta());
            Mix thetaKMaisUm = new Mix(thetajList.size(), thetajList);
            System.out.println("1");
            boolean stop = true;
            for (int j = 1; j <= thetaK.M; j++) {
                Gauss thetajKMaisUm = thetaKMaisUm.getThetaJ(j);
                thetajKMaisUm = thetajKMaisUm.changew(melhoraw(amostra, thetaK, thetaK.getThetaJ(j)));
                // TODO: ver qual destes é o positivo e o negativo
                double amelhorado = melhoraa(amostra, thetaK, thetaK.getThetaJ(j));
                System.out.println("2");

                thetajKMaisUm = thetajKMaisUm.changea1(amelhorado);
                System.out.println("3");

                thetajKMaisUm = thetajKMaisUm.changea2(-amelhorado);
                System.out.println("4");


                thetajKMaisUm = thetajKMaisUm.changeb1(melhorabdj(amostra, thetaK, j, true).getThetaJ(j).b1);
                System.out.println("5");

                thetajKMaisUm = thetajKMaisUm.changeb2(melhorabdj(amostra, thetaK, j, false).getThetaJ(j).b2);

                thetajKMaisUm = thetajKMaisUm.changesigma(melhorasigma(amostra, thetaK, thetaK.getThetaJ(j), thetajKMaisUm));

                thetaKMaisUm.updateThetaj(j, thetajKMaisUm);


                double deltaB1 = Math.pow(thetajKMaisUm.b1 - thetaK.getThetaJ(j).b1, 2);
                double deltaB2 = Math.pow(thetajKMaisUm.b2 - thetaK.getThetaJ(j).b2, 2);

                System.out.println("deltaB1: " + deltaB1);
                System.out.println("deltaB2: " + deltaB2);

                stop = stop && deltaB1 < 0.000001 && deltaB2 < 0.000001;
            }

            if (stop) {
                return thetaKMaisUm;
            } else {
                thetaK = thetaKMaisUm;
            }
        }
    }

    public static double melhoraw(Amostra pessoas, Mix theta, Gauss thetaj) {
        int K = pessoas.calK();

        double soma = 0;
        for (int i = 0; i < K; i++) {
            List<Ponto> pontos = pessoas.indice(i);
            double r = xij(pontos, theta.M, thetaj, theta);
            soma = soma + r;
        }
        return (1d / K) * soma;
    }

    public static double xij(List<Ponto> pontos, int M, Gauss thetaj, Mix theta) {
        double num = thetaj.w * Mix.probj(pontos, thetaj);
        double den = 0;
        for (int u = 1; u <= M; u++) {
            Gauss thetau = theta.theta().get(u - 1);
            den = den + thetau.w * Mix.probj(pontos, thetau);
        }
        return (num * Math.exp(500)) / (den * Math.exp(500));
    }


    public static double melhoraa(Amostra pessoas, Mix theta, Gauss thetaj) {
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

    public static double melhorasigma(Amostra pessoas, Mix theta, Gauss thetajK, Gauss thetajKPlus) {
        int K = pessoas.calK();
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


    //public Mix simulatedAnnealing(Mix theta, Amostra amostra, boolean isForB1) {
    //  Mix thetaAtual = theta;

//        for (int j = 1; j <= theta.theta().size(); j++) {
    //          thetaAtual = melhorabdj(amostra, thetaAtual, j, isForB1);
    //    }
    //  return thetaAtual;
    //}

    // faz iterações (aquecimento/arrefecimento) para cada j
    private static Mix melhorabdj(Amostra amostra, Mix thetaAtual, int j, boolean isForB1) {
        // primeira iterada é feita com o valor antigo de b (o da iterada k anterior)
        ArrefecimentoResult maxResultTetha = new ArrefecimentoResult(thetaAtual, -Double.MAX_VALUE);
        System.out.println("6");

        Mix thetaAquecido = thetaAtual; // na primeira iteração não há aquecimento
        for (int R = 1; R < 1000; R++) {
            ArrefecimentoResult newTetha = arrefecimento(thetaAquecido, j, amostra, isForB1);


            if (newTetha.logProbabilidade > maxResultTetha.logProbabilidade) {
                // só verifica as condições se se tratar do b2 (como dito no enunciado)
                if (!isForB1 && !verificaCondicoes(thetaAtual, newTetha.theta, j)) {
                    // reinicia o SA com b2j = b2j - 0.2
                    R = 0;
                    Gauss aux = newTetha.theta.getThetaJ(j);
                    newTetha.theta.updateThetaj(j, aux.changeb2(aux.b2 - 0.2));
                    thetaAquecido = newTetha.theta;
                    continue;
                } else {
                    maxResultTetha = newTetha;
                }
            }
            thetaAquecido = aquecimento(thetaAtual, j, isForB1);

        }

        System.out.println("Logprob after cooling (j= " + j + " b1= " + isForB1 + ")" + maxResultTetha.logProbabilidade);
        // caso o bdj fique menor que 0 entao devolve a iterada anterior
        if (isForB1 && maxResultTetha.theta.getThetaJ(j).b1 < 0) {
            return thetaAtual;
        } else if (!isForB1 && maxResultTetha.theta.getThetaJ(j).b2 < 0) {
            return thetaAtual;
        } else {
            return maxResultTetha.theta;
        }
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

    //classe criada para podermos devolver o theta resultante de um processo de arrefecimento e respetiva probabilidade
    static class ArrefecimentoResult {
        Mix theta;
        double logProbabilidade;

        public ArrefecimentoResult(Mix theta, double logProbabilidade) {
            this.theta = theta;
            this.logProbabilidade = logProbabilidade;
        }
    }

    //Método que faz o processe de arrefecimento
    private static ArrefecimentoResult arrefecimento(Mix theta, int j, Amostra amostra, boolean isForB1) {
        int iterada = 1;

        double bdjMaximo;
        if (isForB1) {
            bdjMaximo = theta.getThetaJ(j).b1;
        } else {
            bdjMaximo = theta.getThetaJ(j).b2;
        }

        Mix thetaMaximo = theta;

        double logProbMaximo = Math.log(thetaMaximo.prob(amostra));


        while (true) {

            double vizinhobdj = bdjMaximo + nrandom(-0.01, 0.01);
            Gauss thetajatual = thetaMaximo.getThetaJ(j);
            Gauss thetajvizinho;

            // aqui copia-se o thetaj atual alterando apenas o bdj respetivo
            // para o código não estar duplicado, para b1 e b2, basta esta parte ser diferente e o resto mantém-se
            if (isForB1) {
                thetajvizinho = thetajatual.changeb1(vizinhobdj);
            } else {
                thetajvizinho = thetajatual.changeb2(vizinhobdj);
            }

            //aqui cria-se um novo theta que tem a gaussiana j substituída por uma nova thetaj
            // que já contém o parâmetro b1 ou b2 vizinho
            List<Gauss> thetajList = new ArrayList<>(thetaMaximo.theta());
            thetajList.set(j - 1, thetajvizinho);
            Mix thetaVizinho = new Mix(thetajList.size(), thetajList);

            double logProbVizinho = Math.log(thetaVizinho.prob(amostra));

            // se a probabilidade do theta que contém o bdj vizinho for maior,
            // atualizam-se valores do maximo. Se não, aceita-se o maximo anterior com logProbabilidade 1/10000
            if (logProbVizinho > logProbMaximo) {
                thetaMaximo = thetaVizinho;
                bdjMaximo = vizinhobdj;
                logProbMaximo = logProbVizinho;
            } else if (1 == new Random().nextInt(100)) {
                return new ArrefecimentoResult(thetaMaximo, logProbMaximo);
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


    private static boolean verificaCondicoes(Mix thetak, Mix thetakMaisUm, int j) {
        double b1jk = thetak.getThetaJ(j).b1;
        double b2jk = thetak.getThetaJ(j).b2;
        double b1jkmaisUm = thetakMaisUm.getThetaJ(j).b1;
        double b2jkmaisUm = thetakMaisUm.getThetaJ(j).b2;

        return 0 < b1jkmaisUm && b1jkmaisUm < b2jk && b1jk < b2jkmaisUm && b2jkmaisUm < 5;
    }

}
