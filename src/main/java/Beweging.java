import java.util.ArrayList;

public class Beweging {

    private  int id;
    private int startTijdstip;
    private int eindTijdstip;
    private Coördinaat start;
    private Coördinaat eind;
    private int kraan_id;
    private boolean tussenBeweging;
    ArrayList<Beweging> priorityMoves;

    public Beweging(int id,int startTijdstip,int eindTijdstip ,Coördinaat start, Coördinaat eind, int kraan_id, boolean tussenBeweging) {
        this.id = id;
        this.startTijdstip = startTijdstip;
        this.eindTijdstip = eindTijdstip;
        this.start = start;
        this.eind = eind;
        this.kraan_id = kraan_id;
        this.tussenBeweging = tussenBeweging;
        this.priorityMoves = new ArrayList<>();
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

    public int getKraan_id() {
        return kraan_id;
    }

    public void setKraan_id(int kraan_id) {
        this.kraan_id = kraan_id;
    }

    @Override
    public String toString() {
        return "Beweging{" +
                "id=" + id +
                ", startTijdstip=" + startTijdstip +
                ", eindTijdstip=" + eindTijdstip +
                ", start=" + start +
                ", eind=" + eind +
                ", kraan_id=" + kraan_id +
                ", tussenBeweging=" + tussenBeweging +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTussenBeweging() {
        return tussenBeweging;
    }

    public void setTussenBeweging(boolean tussenBeweging) {
        this.tussenBeweging = tussenBeweging;
    }
}
