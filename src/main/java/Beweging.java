public class Beweging {

    private int startTijdstip;
    private int eindTijdstip;
    private Coördinaat start;
    private Coördinaat eind;

    public Beweging(int startTijdstip,int eindTijdstip ,Coördinaat start, Coördinaat eind) {
        this.startTijdstip = startTijdstip;
        this.eindTijdstip = eindTijdstip;
        this.start = start;
        this.eind = eind;
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

    public int getStartTijdstip() {
        return startTijdstip;
    }

    public void setStartTijdstip(int startTijdstip) {
        this.startTijdstip = startTijdstip;
    }

    public int getEindTijdstip() {
        return eindTijdstip;
    }

    public void setEindTijdstip(int eindTijdstip) {
        this.eindTijdstip = eindTijdstip;
    }

    @Override
    public String toString() {
        return "Beweging{" +
                "startTijdstip=" + startTijdstip +
                ", eindTijdstip=" + eindTijdstip +
                ", start=" + start +
                ", eind=" + eind +
                '}';
    }
}
