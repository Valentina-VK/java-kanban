package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.nio.file.Paths;


public class Managers {

    private static final String PATH_TO_DATA = "src/data/TasksStorage.csv";

    private Managers() {
    }

    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(Paths.get(PATH_TO_DATA));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
