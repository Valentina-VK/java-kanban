package history;

import manager.Managers;
import task.Task;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {
    private static final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Task task = new Task("Test addNewTask", "Test addNewTask description");

    @Test
    void add() {
        historyManager.addHistory(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

}
