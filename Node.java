/**
 * A classe Node representa um nó da Amostra.
 */

public class Node {
    /**
     * i representa o índice da medição
     */
    int i;

    /**
     * t e v representam o tempo e o valor da medição, respetivamente.
     */
    double t, v;

    /**
     * next é um ponteiro para o próximo Node.
     */
    Node next;

    @Override
    public String toString() {
        return "Node [i=" + i + ", t=" + t + ", v=" + v + ", next=" + next + "]";
    }

    /**
     * O construtor cria um Node com os valores de uma medição (índice, tempo e valor).
     */
    public Node(int i, double t, double v) {
        super();
        this.i = i;
        this.t = t;
        this.v = v;
    }


}
