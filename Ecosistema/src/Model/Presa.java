// Presa.java
package Model;

public class Presa extends Animal {

    public Presa(int fila, int columna) {
        super(fila, columna);
    }

    @Override
    public int getTipo() {
        return 1;
    }
}

