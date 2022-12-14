
import java.util.ArrayList;
import java.util.List;

public class Kraan {

    private int x;
    private float y;
    private int ymin;
    private int ymax;
    private int id;
    private int xspeed;
    private int yspeed;
    private int xmax;
    private int xmin;
    private int kraanBewegingAantal;
    private List<Beweging> bewegingLijst;
    private  List<Beweging> addedMovements;
    private int z;

    public Kraan(int x, float y, int ymin, int ymax, int id, int xspeed, int yspeed, int xmax, int xmin) {
        this.x = x;
        this.y = y;
        this.ymin = ymin;
        this.ymax = ymax;
        this.kraanBewegingAantal = 0;
        this.id = id;
        this.xspeed = xspeed;
        this.yspeed = yspeed;
        this.xmax = xmax;
        this.xmin = xmin;
        this.bewegingLijst = new ArrayList<>();
        this.addedMovements = new ArrayList<>();
        this.z = 2;
    }


    public List<Beweging> getBewegingLijst() {
        return bewegingLijst;
    }

    public void setBewegingLijst(List<Beweging> bewegingLijst) {
        this.bewegingLijst = bewegingLijst;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getYmin() {
        return ymin;
    }

    public void setYmin(int ymin) {
        this.ymin = ymin;
    }

    public int getYmax() {
        return ymax;
    }

    public void setYmax(int ymax) {
        this.ymax = ymax;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getXspeed() {
        return xspeed;
    }

    public void setXspeed(int xspeed) {
        this.xspeed = xspeed;
    }

    public int getYspeed() {
        return yspeed;
    }

    public void setYspeed(int yspeed) {
        this.yspeed = yspeed;
    }

    public int getXmax() {
        return xmax;
    }

    public void setXmax(int xmax) {
        this.xmax = xmax;
    }

    public int getXmin() {
        return xmin;
    }

    public void setXmin(int xmin) {
        this.xmin = xmin;
    }



    @Override
    public String toString() {
        return "Kraan{" +
                "x=" + x +
                ", y=" + y +
                ", ymin=" + ymin +
                ", ymax=" + ymax +
                ", id=" + id +
                ", xspeed=" + xspeed +
                ", yspeed=" + yspeed +
                ", xmax=" + xmax +
                ", xmin=" + xmin +
                ", bewegingLijst=" + bewegingLijst +
                '}';
    }


    public List<Beweging> getAddedMovements() {
        return addedMovements;
    }

    public void setAddedMovements(List<Beweging> addedMovements) {
        this.addedMovements = addedMovements;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getKraanBewegingAantal() {
        return kraanBewegingAantal;
    }

    public void setKraanBewegingAantal(int kraanBewegingAantal) {
        this.kraanBewegingAantal = kraanBewegingAantal;
    }
}
