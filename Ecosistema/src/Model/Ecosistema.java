package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ecosistema {

    private final int TAM = 10;
    private final Celda[][] matriz;
    private final Random random = new Random();

    public Ecosistema() {
        matriz = new Celda[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                matriz[i][j] = new Celda();
            }
        }
    }

    public Celda[][] getMatriz() {
        return matriz;
    }

    // -------- ESCENARIO INICIAL --------
    public void generarEscenario(int presas, int depredadores) {

        // Limpio cualquier estado anterior
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                matriz[i][j].setAnimal(null);
            }
        }

        // Colocar presas
        for (int k = 0; k < presas; k++) {
            int f, c;
            do {
                f = random.nextInt(TAM);
                c = random.nextInt(TAM);
            } while (!matriz[f][c].estaVacia());

            matriz[f][c].setAnimal(new Presa(f, c));
        }

        // Colocar depredadores
        for (int k = 0; k < depredadores; k++) {
            int f, c;
            do {
                f = random.nextInt(TAM);
                c = random.nextInt(TAM);
            } while (!matriz[f][c].estaVacia());

            matriz[f][c].setAnimal(new Depredador(f, c));
        }
    }

    // -------- MATRIZ NUMÉRICA PARA LA VISTA --------
    public int[][] getMatrizNumerica() {
        int[][] m = new int[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (matriz[i][j].estaVacia()) {
                    m[i][j] = 0;
                } else {
                    m[i][j] = matriz[i][j].getAnimal().getTipo();
                }
            }
        }
        return m;
    }

    // ==========================
    //  FASE 1: SOLO DEPREDADORES
    // ==========================
    public void moverSoloDepredadores() {

        // Tomo una "foto" de dónde están los depredadores ANTES de mover
        List<int[]> posicionesDepredadores = new ArrayList<>();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia() && matriz[i][j].getAnimal() instanceof Depredador) {
                    posicionesDepredadores.add(new int[]{i, j});
                }
            }
        }

        // Ahora los muevo UNO POR UNO sobre el estado actual
        for (int[] pos : posicionesDepredadores) {
            int f = pos[0];
            int c = pos[1];

            if (!matriz[f][c].estaVacia() && matriz[f][c].getAnimal() instanceof Depredador) {
                Depredador d = (Depredador) matriz[f][c].getAnimal();
                moverDepredador(f, c, d);
            }
        }
    }

   
    //  FASE 2: SOLO PRESAS
    public void moverSoloPresas() {

        // Tomo una "foto" de dónde están las presas DESPUÉS de los depredadores
        List<int[]> posicionesPresas = new ArrayList<>();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia() && matriz[i][j].getAnimal() instanceof Presa) {
                    posicionesPresas.add(new int[]{i, j});
                }
            }
        }

        // Muevo cada presa UNA sola vez
        for (int[] pos : posicionesPresas) {
            int f = pos[0];
            int c = pos[1];

            if (!matriz[f][c].estaVacia() && matriz[f][c].getAnimal() instanceof Presa) {
                Presa p = (Presa) matriz[f][c].getAnimal();
                moverPresa(f, c, p);
            }
        }
    }

    // ---------- MOVIMIENTO INTERNO ----------
    private void moverPresa(int fila, int col, Presa presa) {
        List<int[]> libres = obtenerCeldasLibresAdyacentes(fila, col);

        if (libres.isEmpty()) {
            return; // no se mueve
        }

        int[] destino = libres.get(random.nextInt(libres.size()));
        int nf = destino[0];
        int nc = destino[1];

        matriz[nf][nc].setAnimal(presa);
        matriz[fila][col].setAnimal(null);

        presa.setFila(nf);
        presa.setColumna(nc);
    }

    private void moverDepredador(int fila, int col, Depredador depredador) {

        // 1) Buscar presas adyacentes
        List<int[]> presas = obtenerPresasAdyacentes(fila, col);

        if (!presas.isEmpty()) {
            int[] destino = presas.get(random.nextInt(presas.size()));
            int nf = destino[0];
            int nc = destino[1];

            // Se come a la presa -> la reemplaza
            matriz[nf][nc].setAnimal(depredador);
            matriz[fila][col].setAnimal(null);

            depredador.setFila(nf);
            depredador.setColumna(nc);

            // 💡 Reinicio contador de hambre y marco que sí ha comido
            depredador.reiniciarHambre();

            return;
        }

        // 2) Si no hay presas, busca una celda vacía
        List<int[]> libres = obtenerCeldasLibresAdyacentes(fila, col);

        if (!libres.isEmpty()) {
            int[] destino = libres.get(random.nextInt(libres.size()));
            int nf = destino[0];
            int nc = destino[1];

            matriz[nf][nc].setAnimal(depredador);
            matriz[fila][col].setAnimal(null);

            depredador.setFila(nf);
            depredador.setColumna(nc);
        }
    }

    private List<int[]> obtenerCeldasLibresAdyacentes(int fila, int col) {
        List<int[]> libres = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : dirs) {
            int nf = fila + d[0];
            int nc = col + d[1];
            if (nf >= 0 && nf < TAM && nc >= 0 && nc < TAM) {
                if (matriz[nf][nc].estaVacia()) {
                    libres.add(new int[]{nf, nc});
                }
            }
        }
        return libres;
    }

    private List<int[]> obtenerPresasAdyacentes(int fila, int col) {
        List<int[]> presas = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : dirs) {
            int nf = fila + d[0];
            int nc = col + d[1];
            if (nf >= 0 && nf < TAM && nc >= 0 && nc < TAM) {
                if (!matriz[nf][nc].estaVacia() && matriz[nf][nc].getAnimal() instanceof Presa) {
                    presas.add(new int[]{nf, nc});
                }
            }
        }
        return presas;
    }
    // Se llama al final de cada turno completo (después de mover depredadores y presas)

    public void aplicarFinDeTurnoBasico() {

        // Listas de animales que van a reproducirse
        List<Presa> presasReproductoras = new ArrayList<>();
        List<Depredador> depredadoresReproductores = new ArrayList<>();

        // 1) Aumentar contadores y aplicar muerte por hambre
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia()) {
                    Animal a = matriz[i][j].getAnimal();
                    a.aumentarTurno(); // +1 turno vivo, +1 sin comer, +1 desde reproducción

                    if (a instanceof Depredador dep) {
                        // Muerte por hambre: 3 turnos sin comer
                        if (dep.getTurnosSinComer() >= 3) {
                            matriz[i][j].setAnimal(null);
                            continue; // ya no puede reproducirse, está muerto
                        }

                        // Reproducción de depredador:
                        // - Ha comido alguna vez
                        // - Ha comido al menos una vez en los últimos 3 turnos
                        //   -> equivale a turnosSinComer <= 2
                        // - Han pasado al menos 3 turnos desde su última reproducción
                        if (dep.haComidoAlgunaVez()
                                && dep.getTurnosSinComer() <= 2
                                && dep.getTurnosDesdeReproduccion() >= 3) {
                            depredadoresReproductores.add(dep);
                        }

                    } else if (a instanceof Presa presa) {
                        // Reproducción de presa:
                        // - Ha sobrevivido al menos 2 turnos
                        // - Han pasado al menos 2 turnos desde su última reproducción
                        if (presa.getTurnosVivo() >= 2
                                && presa.getTurnosDesdeReproduccion() >= 2) {
                            presasReproductoras.add(presa);
                        }
                    }
                }
            }
        }

        // 2) Reproducción de presas: en celdas vacías adyacentes
        for (Presa madre : presasReproductoras) {
            reproducirPresa(madre);
        }

        // 3) Reproducción de depredadores: en celdas vacías adyacentes
        for (Depredador padre : depredadoresReproductores) {
            reproducirDepredador(padre);
        }
    }

    private void reproducirPresa(Presa madre) {
        List<int[]> libres = obtenerCeldasLibresAdyacentes(madre.getFila(), madre.getColumna());

        if (libres.isEmpty()) {
            return; // no hay espacio para la cría
        }

        int[] destino = libres.get(random.nextInt(libres.size()));
        int nf = destino[0];
        int nc = destino[1];

        Presa hijo = new Presa(nf, nc);
        matriz[nf][nc].setAnimal(hijo);

        // Reiniciamos el contador de reproducción de la madre
        madre.reiniciarReproduccion();
    }

    private void reproducirDepredador(Depredador padre) {
        List<int[]> libres = obtenerCeldasLibresAdyacentes(padre.getFila(), padre.getColumna());

        if (libres.isEmpty()) {
            return; // no hay espacio
        }

        int[] destino = libres.get(random.nextInt(libres.size()));
        int nf = destino[0];
        int nc = destino[1];

        Depredador hijo = new Depredador(nf, nc);
        matriz[nf][nc].setAnimal(hijo);

        // Reiniciamos contador de reproducción del padre
        padre.reiniciarReproduccion();
    }

}
