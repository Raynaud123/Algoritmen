
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kraan {

    List<Beweging> bewegingLijst;
    Map<Integer, Coördinaat> traject;

    public Kraan(List<Beweging> bewegingLijst) {
        this.bewegingLijst = bewegingLijst;
        this.traject = new HashMap<>();
    }

    public void voegBewegingToe(Beweging b) {
        this.bewegingLijst.add(b);

        // 'Trajectories T = [t1:p1, t2:p2, ..., tk:pk] ' , maar klopt lik nog niet helemaal
        traject.put(b.getTijdstip(), b.getStart());
    }

    public Beweging getBewegingOpTijdstip(int t) {
        Beweging beweging = null;
        for (Beweging b : bewegingLijst) {
            if (b.getTijdstip() == t) beweging = b;
        }
        return beweging;
    }

    public List<Beweging> getBewegingLijst() {
        return bewegingLijst;
    }

    public void setBewegingLijst(List<Beweging> bewegingLijst) {
        this.bewegingLijst = bewegingLijst;
    }
}
