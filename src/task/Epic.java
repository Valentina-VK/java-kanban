package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> listIdOfSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
        listIdOfSubtasks = new ArrayList<>();
    }

    public void addSubtask(int idOfSubtask) {
        if (idOfSubtask == this.id) return;
        if (listIdOfSubtasks.contains(idOfSubtask)) return;
        listIdOfSubtasks.add(idOfSubtask);
    }

    public void deleteSubtask(Integer idOfSubtask) {
        listIdOfSubtasks.remove(idOfSubtask);
    }

    public void deleteAllSubtasks() {
        listIdOfSubtasks.clear();
    }

    public List<Integer> getListIdOfSubtasks() {
        return listIdOfSubtasks;
    }
}