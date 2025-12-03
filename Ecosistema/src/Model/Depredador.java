// Depredador.java
package Model;

public class Depredador extends Animal {

    public Depredador(int fila, int columna) {
        super(fila, columna);
    }

    @Override
    public int getTipo() {
        return 2;
    }
}

