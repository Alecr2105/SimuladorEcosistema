package Model;

public class Celda {

    private Animal animal;

    public Celda() {
        this.animal = null;
    }

    public boolean estaVacia() {
        return animal == null;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}

