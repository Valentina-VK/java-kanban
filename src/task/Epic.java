package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> listIdOfSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        listIdOfSubtasks = new ArrayList<>();
    }

    public void addSubtask(int idOfSubtask) {
        if (idOfSubtask == this.id) return;
        if (listIdOfSubtasks.contains(idOfSubtask)) return;
        listIdOfSubtasks.add(idOfSubtask);
    }

    public void deleteSubtask(int idOfSubtask) {
        listIdOfSubtasks.remove(idOfSubtask);
    }

    public void deleteAllSubtasks() {
        listIdOfSubtasks.clear();
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
