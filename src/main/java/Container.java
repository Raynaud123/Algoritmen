public class Container {
    int id;
    int length;


    public Container(Object id, Object length) {
        this.id = ((Long) id).intValue();
        this.length = ((Long) length).intValue();
    }

    @Override
    public String toString() {
        return "Container{" +
                "id=" + id +
                ", length=" + length +
                '}';
    }
}
