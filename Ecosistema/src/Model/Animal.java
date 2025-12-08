package Model;

public abstract class Animal {

    protected int fila;
    protected int columna;

    //Reglas de hambre/reproducción:
    protected int turnosVivo;
    protected int turnosSinComer;
    protected boolean haComidoAlgunaVez;   //Depredadores y su reproducción
    protected int turnosDesdeReproduccion; //Espaciar reproducciones

    public Animal(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.turnosVivo = 0;
        this.turnosSinComer = 0;
        this.haComidoAlgunaVez = false;
        this.turnosDesdeReproduccion = 0;
    }

    
    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    public void setFila(int fila) { this.fila = fila; }
    public void setColumna(int columna) { this.columna = columna; }

    
    
    //Este método se llama al final de cada turno completo:
    public void aumentarTurno() {
        turnosVivo++;
        turnosSinComer++;
        turnosDesdeReproduccion++;
    }

    
    //Cuando come (depredador):
    public void reiniciarHambre() {
        turnosSinComer = 0;
        haComidoAlgunaVez = true;
    }

    public int getTurnosVivo() {
        return turnosVivo;
    }

    public int getTurnosSinComer() {
        return turnosSinComer;
    }

    public boolean haComidoAlgunaVez() {
        return haComidoAlgunaVez;
    }

    public int getTurnosDesdeReproduccion() {
        return turnosDesdeReproduccion;
    }

    public void reiniciarReproduccion() {
        turnosDesdeReproduccion = 0;
    }

    public abstract int getTipo();
}
