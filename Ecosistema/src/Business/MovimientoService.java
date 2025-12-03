package Business;

import Model.Ecosistema;

public class MovimientoService {

    public void moverDepredadores(Ecosistema ecosistema) {
        // delega en la lógica ya implementada en el modelo
        ecosistema.moverSoloDepredadores();
    }

    public void moverPresas(Ecosistema ecosistema) {
        // delega en la lógica ya implementada en el modelo
        ecosistema.moverSoloPresas();
    }
}

