package manager;

import history.HistoryManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ManagersTest {

    @Test
    void ManagerReturnTaskManager() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @Test
    void ManagerReturnHistoryManager() {
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory());
    }
}