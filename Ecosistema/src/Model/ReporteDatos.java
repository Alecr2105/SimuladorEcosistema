package Model;
public class ReporteDatos {
    
    private int totalTurnos;
    private int presasFinales;
    private int depredadoresFinales;
    private int terceraEspecieFinal;
    private Integer turnoExtincionPresas;
    private Integer turnoExtincionDepredadores;
    private int celdasOcupadas;
    private final int totalCeldas;

    public ReporteDatos(int totalCeldas) {
        this.totalCeldas = totalCeldas;
    }

    public int getTotalTurnos() {
        return totalTurnos;
    }

    public void setTotalTurnos(int totalTurnos) {
        this.totalTurnos = totalTurnos;
    }

    public int getPresasFinales() {
        return presasFinales;
    }

    public void setPresasFinales(int presasFinales) {
        this.presasFinales = presasFinales;
    }

    public int getDepredadoresFinales() {
        return depredadoresFinales;
    }

    public void setDepredadoresFinales(int depredadoresFinales) {
        this.depredadoresFinales = depredadoresFinales;
    }

    public int getTerceraEspecieFinal() {
        return terceraEspecieFinal;
    }

    public void setTerceraEspecieFinal(int terceraEspecieFinal) {
        this.terceraEspecieFinal = terceraEspecieFinal;
    }

    public Integer getTurnoExtincionPresas() {
        return turnoExtincionPresas;
    }

    public void setTurnoExtincionPresas(Integer turnoExtincionPresas) {
        this.turnoExtincionPresas = turnoExtincionPresas;
    }

    public Integer getTurnoExtincionDepredadores() {
        return turnoExtincionDepredadores;
    }

    public void setTurnoExtincionDepredadores(Integer turnoExtincionDepredadores) {
        this.turnoExtincionDepredadores = turnoExtincionDepredadores;
    }

    public int getCeldasOcupadas() {
        return celdasOcupadas;
    }

    public void setCeldasOcupadas(int celdasOcupadas) {
        this.celdasOcupadas = celdasOcupadas;
    }

    public int getTotalCeldas() {
        return totalCeldas;
    }

    public double getPorcentajeOcupacion() {
        if (totalCeldas == 0) {
            return 0;
        }
        return (celdasOcupadas * 100.0) / totalCeldas;
    }
    
}
