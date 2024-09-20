import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> listIdOfSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        listIdOfSubtasks = new ArrayList<>();
    }

    public void addSubtask(int idOfSubtask) {
        if (listIdOfSubtasks.contains(idOfSubtask)) return;
        listIdOfSubtasks.add(idOfSubtask);
    }

    public ArrayList<Integer> getListIdOfSubtasks() {
        return listIdOfSubtasks;
    }

    @Override
    public String toString() {
        return String.format("id: %d, name: %s, description.length: %d, status: %s; NumberOfSubtasks:%d",
                id, name, description.length(), status, listIdOfSubtasks.size());
    }
}
