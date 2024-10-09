package history;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT_OF_HISTORY = 10;
    private final List<Task> history = new LinkedList<>();


    @Override
    public void addHistory(Task task) {
        if (task != null) {
            if (history.size() == LIMIT_OF_HISTORY) {
                history.removeFirst();
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

}
