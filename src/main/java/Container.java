public class Container {
    private int id;
    private int length;
    private int slot_id;
    private int target_id;
    private int hoogte;

    public Container(Object id, Object length) {
        this.id = ((Long) id).intValue();
        this.length = ((Long) length).intValue();
        slot_id = Integer.MIN_VALUE;
        hoogte = Integer.MIN_VALUE;
        target_id = Integer.MIN_VALUE;
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

    public int getHoogte() {
        return hoogte;
    }

    public void setHoogte(int hoogte) {
        this.hoogte = hoogte;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }
}
