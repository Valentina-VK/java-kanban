public class Subtask extends Task {
    private int idOfEpic;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    @Override
    public String toString() {
        return String.format("id: %d, name: %s, description.length: %d, status: %s; idOfEpic:%d",
                id, name, description.length(), status, idOfEpic);
    }
}
