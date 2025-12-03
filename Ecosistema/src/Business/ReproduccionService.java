package Business;

import Model.Ecosistema;

public class ReproduccionService {

    // Punto de extensión: si luego decides separar la lógica de reproducción,
    // podrías moverla desde Ecosistema.aplicarFinDeTurnoBasico() hasta aquí.
    // Por ahora, solo lo dejamos como "gancho" para mantener la arquitectura
    // que pidió el profesor.
    public void aplicarReglasReproduccion(Ecosistema ecosistema) {
        // actualmente la reproducción ya se maneja dentro de
        // Ecosistema.aplicarFinDeTurnoBasico()
        // Aquí podrías, en el futuro, mover esa parte de la lógica.
    }
}

