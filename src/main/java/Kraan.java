
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kraan {

    private int x;
    private int startX;
    private float y;
    private float startY;
    private int ymin;
    private int ymax;
    private int id;
    private int xspeed;
    private int yspeed;
    private int xmax;
    private int xmin;
    private List<Beweging> bewegingLijst;

    public Kraan(int x, float y, int ymin, int ymax, int id, int xspeed, int yspeed, int xmax, int xmin) {
        this.x = x;
        this.startX = x;
        this.y = y;
        this.startY = y;
        this.ymin = ymin;
        this.ymax = ymax;
        this.id = id;
        this.xspeed = xspeed;
        this.yspeed = yspeed;
        this.xmax = xmax;
        this.xmin = xmin;
        this.bewegingLijst = new ArrayList<>();
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

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }
}
