/**
 * A classe Gauss representa uma gaussiana, ou seja, um thetaj.
 */
public class Gauss {
    /**
     * Estes atributos são os parâmetros que definem a gaussiana.
     * Final não permite que os atributos sejam alterados depois de terem sido definidos no construtor
     */
    final double w, sig, a1, a2, b1, b2;

    /**
     * O método construtor recebe os parâmetros da gaussiana e constrói uma Gauss a partir destes.
     */
    public Gauss(double w, double sig, double a1, double a2, double b1, double b2) {
        this.w = w;
        this.sig = sig;
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public String toString() {
        return "Gauss{" +
                "w=" + w +
                ", sig=" + sig +
                ", a1=" + a1 +
                ", a2=" + a2 +
                ", b1=" + b1 +
                ", b2=" + b2 +
                '}';
    }

    public Gauss changeb1(double newB1) {
        return new Gauss(w, sig, a1, a2, newB1, b2);
    }

    public Gauss changeb2(double newB2) {
        return new Gauss(w, sig, a1, a2, b1, newB2);
    }

    public Gauss changew(double newW) {
        return new Gauss(newW, sig, a1, a2, b1, b2);
    }

    public Gauss changesigma(double newSigma) {
        return new Gauss(w, newSigma, a1, a2, b1, b2);
    }

    public Gauss changea1(double newA1) {
        return new Gauss(w, sig, newA1, a2, b1, b2);
    }

    public Gauss changea2(double newA2) {
        return new Gauss(w, sig, a1, newA2, b1, b2);
    }

}


