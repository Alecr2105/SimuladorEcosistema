package Model;

public class TerceraEspecie extends Animal {

    private String variante; // "Mutante", "AliadaPresas", "AliadaDepredadores"

    public TerceraEspecie(int fila, int columna, String variante) {
        super(fila, columna);
        this.variante = variante;
    }

    public String getVariante() {
        return variante;
    }

    public void setVariante(String variante) {
        this.variante = variante;
    }

    @Override
    public int getTipo() {
        return 3; // en la matriz será 3
    }
}

