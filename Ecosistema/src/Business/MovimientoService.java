package Business;

import Model.Ecosistema;

public class MovimientoService {

    public void moverDepredadores(Ecosistema ecosistema) {
        // delega en la lógica del modelo
        ecosistema.moverSoloDepredadores();
    }

    public void moverPresas(Ecosistema ecosistema) {
        // delega en la lógica del modelo
        ecosistema.moverSoloPresas();
    }

    // No movemos aquí la tercera especie.
    // Eso ya lo hace EcosistemaService.moverTerceraEspecie()
}
