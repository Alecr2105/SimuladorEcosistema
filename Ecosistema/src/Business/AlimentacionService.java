package Business;

// Aplica las reglas al final del turno:
// - aumenta contadores de turnos / hambre / reproducción,
// - mata depredadores por hambre,
// - decide qué animales se reproducen y crea las crías.

import Model.Ecosistema;

public class AlimentacionService {
    
    public void aplicarReglasAlimentacionYFinTurno(Ecosistema ecosistema) {
        ecosistema.aplicarFinDeTurnoBasico();
    }
}

