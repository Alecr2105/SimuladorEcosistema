package Model;

public class Depredador extends Animal {

    private boolean furioso;

    public Depredador(int fila, int columna) {
        super(fila, columna);
        this.furioso = false;
    }

    @Override
    public int getTipo() {
        return 2;
    }

    public boolean isFurioso() {
        return furioso;
    }

    public void setFurioso(boolean furioso) {
        this.furioso = furioso;
    }
}


