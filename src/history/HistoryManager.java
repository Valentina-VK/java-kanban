package history;

import task.Task;

import java.util.List;

public interface HistoryManager {
    void addHistory(Task task);

    List<Task> getHistory();
}
