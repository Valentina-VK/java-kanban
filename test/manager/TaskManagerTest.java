package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTestManager();

    @BeforeEach
    void initialize() {
        taskManager = createTestManager();
    }

    @Test
    void reduceEpicStatusIfAllSubtasksNew() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.NEW, epicId);
        Subtask subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.NEW, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.NEW, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicStatusIfAllSubtasksDone() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.DONE, epicId);
        Subtask subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.DONE, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.DONE, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicStatusIfAllSubtasksInProgress() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.IN_PROGRESS, epicId);
        Subtask subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.IN_PROGRESS, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicStatusIfSubtasksNewAndDone() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.NEW, epicId);
        Subtask subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.DONE, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicTime() {
        LocalDateTime start = LocalDateTime.now();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                start, 30, Status.NEW, epicId);
        Subtask subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                start.plusHours(1), 30, Status.NEW, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(start, taskManager.getTaskByID(epicId).getStartTime(), "StartTime of Epic is not correct");
        assertEquals(subtask2.getEndTime(), taskManager.getTaskByID(epicId).getEndTime(), "EndTime of Epic is not correct");

        Duration allSubTaskDuration = subtask1.getDuration().plus(subtask2.getDuration());
        assertEquals(allSubTaskDuration, taskManager.getTaskByID(epicId).getDuration(), "Duration of Epic is not correct");
    }

    @Test
    void detectTaskTimeOverlap() {
        LocalDateTime startTime = LocalDateTime.now();
        Task task0 = new Task("Test Task0", "Test description", startTime, 30);
        taskManager.addTask(task0);
        Task task1 = new Task("Test Task1", "Test description", startTime.plus(Duration.ofMinutes(15)), 30);
        Task task2 = new Task("Test Task2", "Test description", startTime.minus(Duration.ofMinutes(15)), 30);
        Task task3 = new Task("Test Task3", "Test description", startTime.minus(Duration.ofMinutes(35)), 30);
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.addTask(task1), "Overlap is not detected");
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.addTask(task2), "Overlap is not detected");
        assertDoesNotThrow(() -> taskManager.addTask(task3), "Overlap is not detected");
    }

    @Test
    void getListOfTaskSortedByPriority() {
        LocalDateTime startTime = LocalDateTime.now();
        Task taskNoTime = new Task("Test Task0", "Test description");
        Task taskSecond = new Task("Test Task1", "Test description", startTime, 30);
        Task taskThird = new Task("Test Task2", "Test description", startTime.plusHours(1), 30);
        Task taskFirst = new Task("Test Task3", "Test description", startTime.minusHours(1), 30);
        taskManager.addTask(taskSecond);
        taskManager.addTask(taskThird);
        taskManager.addTask(taskFirst);
        assertFalse(taskManager.getPrioritizedTasks().contains(taskNoTime), "Task without time is in List");
        assertEquals(taskFirst, taskManager.getPrioritizedTasks().getFirst(), "Not First Task");
        assertEquals(taskThird, taskManager.getPrioritizedTasks().getLast(), "Not Last Task");
    }
}