package Model;

public class Presa extends Animal {

    private boolean venenosa;

    public Presa(int fila, int columna) {
        super(fila, columna);
        this.venenosa = false;
    }

    @Override
    public int getTipo() {
        return 1;
    }

    public boolean isVenenosa() {
        return venenosa;
    }

    public void setVenenosa(boolean venenosa) {
        this.venenosa = venenosa;
    }
}


