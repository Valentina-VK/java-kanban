package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

public class Managers {
    static TaskManager manager = new InMemoryTaskManager();
    static HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
