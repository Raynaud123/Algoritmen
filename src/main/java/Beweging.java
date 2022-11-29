public class Beweging {

    private int tijdstip;
    private Coördinaat start;
    private Coördinaat eind;
    private int snelheid;

    public Beweging(int tijdstip, Coördinaat start, Coördinaat eind, int snelheid) {
        this.tijdstip = tijdstip;
        this.start = start;
        this.eind = eind;
        this.snelheid = snelheid;
    }

    public int getTijdstip() {
        return tijdstip;
    }

    public void setTijdstip(int tijdstip) {
        this.tijdstip = tijdstip;
    }

    public Coördinaat getStart() {
        return start;
    }

    public void setStart(Coördinaat start) {
        this.start = start;
    }

    public Coördinaat getEind() {
        return eind;
    }

    public void setEind(Coördinaat eind) {
        this.eind = eind;
    }

    public int getSnelheid() {
        return snelheid;
    }

    public void setSnelheid(int snelheid) {
        this.snelheid = snelheid;
    }
}
