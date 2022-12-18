public class Container {
    int id;
    int length;
    int slot_id;


    public Container(Object id, Object length) {
        this.id = ((Long) id).intValue();
        this.length = ((Long) length).intValue();
        slot_id = Integer.MIN_VALUE;
    }

    @Override
    public String toString() {
        return "Container{" +
                "id=" + id +
                ", length=" + length +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(int slot_id) {
        this.slot_id = slot_id;
    }
}
