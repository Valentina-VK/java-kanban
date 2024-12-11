package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> listIdOfSubtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
        listIdOfSubtasks = new ArrayList<>();
        endTime = LocalDateTime.of(1, 1, 1, 0, 0);
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

    public void setTimeFields(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}