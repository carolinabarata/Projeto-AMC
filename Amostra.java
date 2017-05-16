
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Esta classe foi implementada como uma lista ligada, utilizando a classe Node,
 * que representa cada um dos elementos presentes na Amostra.
 */
public class Amostra {
    /**
     * first guarda o primeiro Node da Amostra, e o last, o último.
     */
    private Node first, last;

    /**
     * length guarda o tamanho da Amostra, ou seja, o nº de nós que a constituem.
     */
    private int length;

    private Map<Integer, List<Ponto>> fastMap;

    @Override
    public String toString() {
        return "Amostra [first=" + first + ", length=" + length + "]";
    }


    /**
     * O construtor cria uma amostra vazia.
     */
    public Amostra() {
        super();
        this.first = null;
        this.last = null;
        this.length = 0;
        this.fastMap = new HashMap<>();
    }
    /**
     * Este método recebe os valores de uma medição (índice, tempo e valor)
     * e adiciona este elemento à amostra (sob a forma de Node).
     * A inserção do novo elemento é feita no início da Amostra.
     */
    public void add(int i, double t, double v) {
        Node n = new Node(i, t, v);
        n.next = first;
        first = n;
        List<Ponto> lista = fastMap.get(i);
        if(lista == null){
            lista = new ArrayList<>();
        }
        lista.add(new Ponto(t, v));
        fastMap.put(i, lista);

        if (length == 0) {
            last = n;
        }
        length++;
    }



    public List<Ponto> slowIndice(int ind) {
        Node aux = first;
        List<Ponto> lista = new ArrayList<>();

        while (aux != null) {
            if (aux.i == ind) {
                Ponto ponto = new Ponto(aux.t, aux.v);
                lista.add(ponto);
            }
            aux = aux.next;
        }
        return lista;
    }

    public List<Ponto> fastIndice(int ind){
        return fastMap.get(ind);
    }

    public List<Ponto> indice(int ind){
        return fastIndice(ind);
    }

    /**
     * Este método devolve o comprimento da Amostra.
     */
    public int length() {
        return length;
    }

    /**
     * Este método recebe uma posição, e devolve um array com a medição correspondente a essa posição
     * (índice, tempo e valor). É considerado que a primeira posição da Amostra é a posição 1.
     * No caso de a posição dada não existir na Amostra, é devolvido o array com todas as posições iguais a -1.
     */
    public double[] element(int p) {
        double[] array = new double[3];
        if (length == 0 || p > length || p < 1) {
            array[0] = -1;
            array[1] = -1;
            array[2] = -1;
        } else {
            int k = 1;
            Node aux = first;
            while (k != p) {
                k++;
                aux = aux.next;
            }
            array[0] = aux.i;
            array[1] = aux.t;
            array[2] = aux.v;
        }
        return array;
    }

    /**
     * Este método recebe um índice, e devolve uma lista de arrays,
     * onde cada array é constituído por um par (tempo, valor) correspondentes a esse índice.
     * Estamos a assumir que podem então existir na Amostra vários índices iguais.
     */
    public List<double[]> indiceoldversion(int ind) {
        Node aux = first;

        List<double[]> lista = new ArrayList<>();

        while (aux != null) {
            double[] par = new double[2];
            if (aux.i == ind) {

                par[0] = aux.t;
                par[1] = aux.v;
                lista.add(par);
            }
            aux = aux.next;
        }
        return lista;
    }

    public List<Ponto> indiceSlow(int ind) {
        Node aux = first;
        List<Ponto> lista = new ArrayList<>();

        while (aux != null) {
            if (aux.i == ind) {
                Ponto ponto = new Ponto(aux.t, aux.v);
                lista.add(ponto);
            }
            aux = aux.next;
        }
        return lista;
    }


    /**
     * Este método recebe uma Amostra e cria uma nova Amostra que é a concatenação destas,
     * sem alterar as duas Amostras iniciais.
     */
    public Amostra join(Amostra other) {
        Amostra c = new Amostra();
        copyAndAppend(this, c);
        copyAndAppend(other, c);
        return c;
    }

    /**
     * Método auxiliar que recebe duas Amostras e copia todos os elementos de source para destination.
     */
    private void copyAndAppend(Amostra source, Amostra destination) {
        Node aux = source.first;
        while (aux != null) {
            destination.append(aux.i, aux.t, aux.v);
            aux = aux.next;

        }
    }

    /**
     * Método auxiliar que recebe os valores de uma medição (índice, tempo e valor)
     * e adiciona este elemento à amostra (sob a forma de Node).
     * A inserção do novo elemento é feita no fim da Amostra.
     */
    private void append(int i, double t, double v) {
        Node n = new Node(i, t, v);
        if (length == 0) {
            first = n;
            last = n;
        } else {
            last.next = n;
            last = n;
        }
        length++;
    }

//TODO verificar se isto funciona
    public int calK(){
        int soma = 1;
        Node aux = first;
        if(first == null)
            return 0;
        while(aux.next != null){
            if(aux.next.i != aux.i ){
                soma++;
            }
            aux = aux.next;
        }
        return soma;
    }

}
