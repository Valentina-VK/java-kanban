package history;

import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Task task1;
    private Task task2;
    private Epic task3;
    private Subtask task4;
    private Subtask task5;
    private int testId = 1000;

    @BeforeEach
    void createTasks() {
        task1 = new Task("New Task1", "Test description 1");
        task1.setId(testId++);
        task2 = new Task("New Task1", "Test description 1");
        task2.setId(testId++);
        task3 = new Epic("New Task1", "Test description 1");
        task3.setId(testId++);
        task4 = new Subtask("New Task1", "Test description 1", Status.NEW, task3.getId());
        task4.setId(testId++);
        task3.addSubtask(task4.getId());
        task5 = new Subtask("New Task1", "Test description 1", Status.NEW, task3.getId());
        task5.setId(testId++);
        task3.addSubtask(task5.getId());
    }

    @Test
    void add() {
        historyManager.addHistory(task1);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История не добавляется.");
        historyManager.addHistory(task3);
        historyManager.addHistory(task5);
        historyManager.addHistory(task2);
        history = historyManager.getHistory();
        assertEquals(4, history.size(), "История не добавляется.");
    }

    @Test
    void addTheSameTaskThreeTimes() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task1);
        historyManager.addHistory(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История той же задачи не перезаписалась.");
    }

    @Test
    void displayInTheOrderTaskWasAdded() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task3);
        historyManager.addHistory(task5);
        historyManager.addHistory(task2);
        historyManager.addHistory(task4);
        historyManager.addHistory(task1);
        List<Task> listForTest = Arrays.asList(task3, task5, task2, task4, task1);
        assertArrayEquals(listForTest.toArray(), historyManager.getHistory().toArray(), "Просмотренные задачи не совпадают");
    }

    @Test
    void removeTaskFromHistory() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.addHistory(task4);
        historyManager.addHistory(task5);
        historyManager.remove(task2.getId());
        List<Task> listForTest = Arrays.asList(task1, task3, task4, task5);
        assertArrayEquals(listForTest.toArray(), historyManager.getHistory().toArray(), "Вторая задача не удалена");
        historyManager.remove(task5.getId());
        listForTest = Arrays.asList(task1, task3, task4);
        assertArrayEquals(listForTest.toArray(), historyManager.getHistory().toArray(), "Последняя-пятая задача не удалена");
        historyManager.remove(task1.getId());
        listForTest = Arrays.asList(task3, task4);
        assertArrayEquals(listForTest.toArray(), historyManager.getHistory().toArray(), "Первая задача не удалена");
    }
}