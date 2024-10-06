package history;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    static final int LIMIT_OF_HISTORY = 10;
    List<Task> history = new ArrayList<>(LIMIT_OF_HISTORY);

    @Override
    public void addHistory(Task task) {
        if (history.size() == LIMIT_OF_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void clearHistory() {
        history.clear();
    }
}
