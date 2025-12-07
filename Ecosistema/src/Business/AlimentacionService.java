package Business;

import Model.Ecosistema;

public class AlimentacionService {

    // Aplica las reglas al final del turno:
    // - aumenta contadores de turnos / hambre / reproducción,
    // - mata depredadores por hambre,
    // - decide qué animales se reproducen y crea las crías.
    public void aplicarReglasAlimentacionYFinTurno(Ecosistema ecosistema) {
        ecosistema.aplicarFinDeTurnoBasico();
    }
}

