package task;

public class Subtask extends Task {
    private int idOfEpic;

    public Subtask(String name, String description, Status status, int idOfEpic) {
        super(name, description);
        this.type = Type.SUBTASK;
        this.status = status;
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public void deleteIdOfEpic() {
        this.idOfEpic = -1;
    }

    @Override
    public void setId(int id) {
        if (id != this.idOfEpic) {
            this.id = id;
        }
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", id, type, name, status, description, idOfEpic);
    }
}