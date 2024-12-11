package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.nio.file.Path;

public class Managers {

    private static final String PATH_TO_DATA = "src/data/TasksStorage.csv";

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultFileBackedTaskManager() {
        return FileBackedTaskManager.loadFromFile(Path.of(PATH_TO_DATA));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}