package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.nio.file.Paths;


public class Managers {

    private static final String pathToData = "src/data/TasksStorage.csv";

    private Managers() {
    }

    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(Paths.get(pathToData));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}