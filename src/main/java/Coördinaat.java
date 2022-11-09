
public class Coördinaat {
    private int id;
    private int x;
    private int y;
//    private int z;

    public Coördinaat(int x, int y, int z, int id) {
        this.x = x;
        this.y = y;
//        this.z = z;
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

//    public int getZ() {
//        return z;
//    }
//
//    public void setZ(int z) {
//        this.z = z;
//    }
}
