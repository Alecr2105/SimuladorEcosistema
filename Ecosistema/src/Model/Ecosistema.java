package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ecosistema {

    private final int TAM = 10;
    private final Celda[][] matriz;
    private final Random random = new Random();
    private final List<String> eventosTurno = new ArrayList<>();

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

    // compatibilidad
    public void generarEscenario(int presas, int depredadores) {
        generarEscenario(presas, depredadores, 0, null, false, null);
    }

    public void generarEscenario(int presas,
            int depredadores,
            int terceras,
            String varianteTercera) {
        generarEscenario(presas, depredadores, terceras, varianteTercera, false, null);
    }

// NUEVO: con tercera especie + mutaciones
    public void generarEscenario(int presas,
            int depredadores,
            int terceras,
            String varianteTercera,
            boolean mutacionesActivas,
            String tipoMutacion) {

        // Limpio
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

        // Tercera especie (si aplica)
        if (terceras > 0 && varianteTercera != null) {
            for (int k = 0; k < terceras; k++) {
                int f, c;
                do {
                    f = random.nextInt(TAM);
                    c = random.nextInt(TAM);
                } while (!matriz[f][c].estaVacia());

                matriz[f][c].setAnimal(new TerceraEspecie(f, c, varianteTercera));
            }
        }

        // Mutaciones (si aplica):
        if (mutacionesActivas && tipoMutacion != null) {
            aplicarMutacionesIniciales(tipoMutacion);
        }
    }

    private void aplicarMutacionesIniciales(String tipoMutacion) {
        double prob = 0.3; // 30% de probabilidad de ser mutado

        if ("FURIA".equals(tipoMutacion)) {
            for (int i = 0; i < TAM; i++) {
                for (int j = 0; j < TAM; j++) {
                    if (!matriz[i][j].estaVacia()
                            && matriz[i][j].getAnimal() instanceof Depredador dep) {
                        if (random.nextDouble() < prob) {
                            dep.setFurioso(true);
                        }
                    }
                }
            }
        } else if ("VENENO".equals(tipoMutacion)) {
            for (int i = 0; i < TAM; i++) {
                for (int j = 0; j < TAM; j++) {
                    if (!matriz[i][j].estaVacia()
                            && matriz[i][j].getAnimal() instanceof Presa presa) {
                        if (random.nextDouble() < prob) {
                            presa.setVenenosa(true);
                        }
                    }
                }
            }
        }
    }

    // Matriz numérica para el JTable (0 vacío, 1 presa, 2 depredador, 3 tercera especie)
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
    //  MOVIMIENTO POR FASES
    // ==========================
    // 1) Solo depredadores
    public void moverSoloDepredadores(Ecosistema ecosistema) {

        List<int[]> posicionesDepredadores = new ArrayList<>();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia()
                        && matriz[i][j].getAnimal() instanceof Depredador) {
                    posicionesDepredadores.add(new int[]{i, j});
                }
            }
        }

        for (int[] pos : posicionesDepredadores) {
            int f = pos[0];
            int c = pos[1];

            if (!matriz[f][c].estaVacia()
                    && matriz[f][c].getAnimal() instanceof Depredador dep) {
                moverDepredador(f, c, dep);
            }
        }
    }

    // 2) Solo presas
    public void moverSoloPresas(Ecosistema ecosistema) {

        List<int[]> posicionesPresas = new ArrayList<>();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia()
                        && matriz[i][j].getAnimal() instanceof Presa) {
                    posicionesPresas.add(new int[]{i, j});
                }
            }
        }

        for (int[] pos : posicionesPresas) {
            int f = pos[0];
            int c = pos[1];

            if (!matriz[f][c].estaVacia()
                    && matriz[f][c].getAnimal() instanceof Presa p) {
                moverPresa(f, c, p);
            }
        }
    }

    // 3) Solo tercera especie (la movemos tipo presa)
    public void moverSoloTerceraEspecie() {

        List<int[]> posiciones = new ArrayList<>();
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia()
                        && matriz[i][j].getAnimal() instanceof TerceraEspecie) {
                    posiciones.add(new int[]{i, j});
                }
            }
        }

        for (int[] pos : posiciones) {
            int f = pos[0];
            int c = pos[1];

            if (!matriz[f][c].estaVacia()
                    && matriz[f][c].getAnimal() instanceof TerceraEspecie especie3) {

                String variante = especie3.getVariante();

                // 1) Intentar atacar según la variante
                int[] destinoAtaque = null;

                switch (variante) {
                    case "Mutante" -> {
                        // puede comerse presas y depredadores
                        List<int[]> objetivos = obtenerAdyacentesPresasYDepredadores(f, c);
                        if (!objetivos.isEmpty()) {
                            destinoAtaque = objetivos.get(random.nextInt(objetivos.size()));
                        }
                    }
                    case "AliadaPresas" -> {
                        // ataca depredadores
                        List<int[]> depreds = obtenerDepredadoresAdyacentes(f, c);
                        if (!depreds.isEmpty()) {
                            destinoAtaque = depreds.get(random.nextInt(depreds.size()));
                        }
                    }
                    case "AliadaDepredadores" -> {
                        // ataca presas
                        List<int[]> presas = obtenerPresasAdyacentes(f, c);
                        if (!presas.isEmpty()) {
                            destinoAtaque = presas.get(random.nextInt(presas.size()));
                        }
                    }
                }

                if (destinoAtaque != null) {
                    int nf = destinoAtaque[0];
                    int nc = destinoAtaque[1];

                    // se mueve a la celda del objetivo y lo elimina
                    matriz[nf][nc].setAnimal(especie3);
                    matriz[f][c].setAnimal(null);

                    especie3.setFila(nf);
                    especie3.setColumna(nc);

                    continue;
                }

                // 2) Si no atacó a nadie, se mueve como una presa (a celda vacía)
                List<int[]> libres = obtenerCeldasLibresAdyacentes(f, c);
                if (libres.isEmpty()) {
                    continue;
                }

                int[] destino = libres.get(random.nextInt(libres.size()));
                int nf = destino[0];
                int nc = destino[1];

                matriz[nf][nc].setAnimal(especie3);
                matriz[f][c].setAnimal(null);

                especie3.setFila(nf);
                especie3.setColumna(nc);
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

            Animal objetivo = matriz[nf][nc].getAnimal();

            // 💀 CASO 1: presa venenosa → mueren ambos
            if (objetivo instanceof Presa p && p.isVenenosa()) {

                // log interno (luego lo sacamos al JTextArea)
                eventosTurno.add("Depredador murió al comer una presa venenosa en (" + nf + "," + nc + ").");

                matriz[fila][col].setAnimal(null); // muere depredador
                matriz[nf][nc].setAnimal(null);    // desaparece la presa
                return;
            }

            // 🥩 CASO 2: presa normal → comportamiento normal
            matriz[nf][nc].setAnimal(depredador);
            matriz[fila][col].setAnimal(null);

            depredador.setFila(nf);
            depredador.setColumna(nc);

            depredador.reiniciarHambre();

            eventosTurno.add("Depredador comió una presa en (" + nf + "," + nc + ").");
            return;
        }
        // 2) Si no hay presas, buscar celda libre (igual que ya lo tenías)

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
                if (!matriz[nf][nc].estaVacia()
                        && matriz[nf][nc].getAnimal() instanceof Presa) {
                    presas.add(new int[]{nf, nc});
                }
            }
        }
        return presas;
    }

    
    //Fin de turnos: Alimentación + reproducción
    public void aplicarFinDeTurnoBasico(Ecosistema ecosistema) {
        List<Presa> presasReproductoras = new ArrayList<>();
        List<Depredador> depredadoresReproductores = new ArrayList<>();

        // 1) Aumentar contadores y aplicar muerte por hambre
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia()) {
                    Animal a = matriz[i][j].getAnimal();
                    a.aumentarTurno(); // +1 turno vida, +1 sin comer, +1 desde reproducción

                    if (a instanceof Depredador dep) {
                        // Muerte por hambre: 3 turnos sin comer
                        if (dep.getTurnosSinComer() >= 3 && !dep.isFurioso()) {
                            matriz[i][j].setAnimal(null);
                            continue;
                        }

                        // Reproducción depredador:
                        // - ha comido alguna vez
                        // - no lleva más de 2 turnos sin comer
                        // - han pasado >=3 turnos desde su última reproducción
                        if (dep.haComidoAlgunaVez()
                                && dep.getTurnosSinComer() <= 2
                                && dep.getTurnosDesdeReproduccion() >= 3) {
                            depredadoresReproductores.add(dep);
                        }

                    } else if (a instanceof Presa presa) {
                        // Reproducción presa:
                        // - ha sobrevivido >=2 turnos
                        // - han pasado >=2 turnos desde su última reproducción
                        if (presa.getTurnosVivo() >= 2
                                && presa.getTurnosDesdeReproduccion() >= 2) {
                            presasReproductoras.add(presa);
                        }
                    }
                }
            }
        }

        // 2) Reproducción de presas
        for (Presa madre : presasReproductoras) {
            reproducirPresa(madre);
        }

        // 3) Reproducción de depredadores
        for (Depredador padre : depredadoresReproductores) {
            reproducirDepredador(padre);
        }
    }

    private void reproducirPresa(Presa madre) {
        List<int[]> libres = obtenerCeldasLibresAdyacentes(madre.getFila(), madre.getColumna());

        if (libres.isEmpty()) {
            return;
        }

        int[] destino = libres.get(random.nextInt(libres.size()));
        int nf = destino[0];
        int nc = destino[1];

        Presa hijo = new Presa(nf, nc);
        matriz[nf][nc].setAnimal(hijo);

        madre.reiniciarReproduccion();
    }

    private void reproducirDepredador(Depredador padre) {
        List<int[]> libres = obtenerCeldasLibresAdyacentes(padre.getFila(), padre.getColumna());

        if (libres.isEmpty()) {
            return;
        }

        int[] destino = libres.get(random.nextInt(libres.size()));
        int nf = destino[0];
        int nc = destino[1];

        Depredador hijo = new Depredador(nf, nc);
        matriz[nf][nc].setAnimal(hijo);

        padre.reiniciarReproduccion();
    }

    private List<int[]> obtenerDepredadoresAdyacentes(int fila, int col) {
        List<int[]> depreds = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : dirs) {
            int nf = fila + d[0];
            int nc = col + d[1];
            if (nf >= 0 && nf < TAM && nc >= 0 && nc < TAM) {
                if (!matriz[nf][nc].estaVacia()
                        && matriz[nf][nc].getAnimal() instanceof Depredador) {
                    depreds.add(new int[]{nf, nc});
                }
            }
        }
        return depreds;
    }

    private List<int[]> obtenerAdyacentesPresasYDepredadores(int fila, int col) {
        List<int[]> objetivos = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : dirs) {
            int nf = fila + d[0];
            int nc = col + d[1];
            if (nf >= 0 && nf < TAM && nc >= 0 && nc < TAM) {
                if (!matriz[nf][nc].estaVacia()) {
                    if (matriz[nf][nc].getAnimal() instanceof Presa
                            || matriz[nf][nc].getAnimal() instanceof Depredador) {
                        objetivos.add(new int[]{nf, nc});
                    }
                }
            }
        }
        return objetivos;
    }

    public int contarPresas() {
        int cont = 0;
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia() && matriz[i][j].getAnimal() instanceof Presa) {
                    cont++;
                }
            }
        }
        return cont;
    }

    public int contarDepredadores() {
        int cont = 0;
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia() && matriz[i][j].getAnimal() instanceof Depredador) {
                    cont++;
                }
            }
        }
        return cont;
    }

    public int contarTerceraEspecie() {
        int cont = 0;
        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                if (!matriz[i][j].estaVacia() && matriz[i][j].getAnimal() instanceof TerceraEspecie) {
                    cont++;
                }
            }
        }
        return cont;
    }

    public List<String> consumirEventosTurno() {
        List<String> copia = new ArrayList<>(eventosTurno);
        eventosTurno.clear();
        return copia;
    }

}
