package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    protected abstract T createTestManager();

    @BeforeEach
    void initialize() {
        taskManager = createTestManager();
        task = new Task("Test addNewTask", "Test addNewTask description");
        epic = new Epic("Test addNewEpic", "Test addNewEpic description");
    }

    @Test
    void addNewTask() {
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getId(), savedTask.getId(), "ID не совпадают.");
        assertEquals(task.getName(), savedTask.getName(), "Name не совпадают.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Description не совпадают.");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Status не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        final int epicId = taskManager.addTask(epic);
        final Epic savedEpic = taskManager.getEpicByID(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        savedEpic.addSubtask(epicId);
        final List<Integer> subtasksOfEpic = savedEpic.getListIdOfSubtasks();

        assertNotNull(subtasksOfEpic, "Задачи не возвращаются.");
        assertEquals(0, subtasksOfEpic.size(), "Неверное количество подзадач.");
    }

    @Test
    void addNewSubtask() {
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        final int subId1 = taskManager.addTask(subtask1);
        subtask2 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, subId1);
        final int subId2 = taskManager.addTask(subtask2);

        final Subtask savedSub = taskManager.getSubtaskByID(subId1);

        assertNotNull(savedSub, "Задача не найдена.");
        assertEquals(subtask1, savedSub, "Задачи не совпадают.");

        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskByID(subId2), "Некорректное сохранение позадачи.");

        final List<Subtask> subtasks = taskManager.getSubTaskList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void notConflictTasksInMemoryTaskManager() {
        int notGenId = 1111111;
        task.setId(notGenId);
        assertThrows(NotFoundException.class, () -> taskManager.updateTask(task), "Некорректное обновление задачи.");
        assertThrows(NotFoundException.class, () -> taskManager.getTaskByID(notGenId), "Некорректное обновление задачи.");

        final List<Task> tasks = taskManager.getTaskList();
        int savedSize = tasks.size();
        taskManager.deleteTaskById(notGenId);
        assertEquals(savedSize, taskManager.getTaskList().size(), "Неверное количество задач.");

        final int newTaskId = taskManager.addTask(task);
        assertNotEquals(newTaskId, notGenId, "Новый ID при добавлении не сгенерирован");
    }

    @Test
    void deleteTaskTest() {
        final int taskId = taskManager.addTask(task);
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        final int subId1 = taskManager.addTask(subtask1);
        subtask2 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        final int subId2 = taskManager.addTask(subtask2);

        taskManager.deleteTaskById(taskId);
        assertThrows(NotFoundException.class, () -> taskManager.getTaskByID(taskId), "Некорректное удаление задачи.");

        taskManager.deleteSubtaskById(subId1);
        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskByID(subId1), "Некорректное удаление подзадачи.");
        assertFalse(epic.getListIdOfSubtasks().contains(subId1), "В Эпике есть ссылка на удаленную подзадачу.");

        taskManager.deleteEpicById(epicId);
        assertThrows(NotFoundException.class, () -> taskManager.getEpicByID(epicId), "Некорректное удаление эпика.");
        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskByID(subId2), "При удалении эпика осталась подзадача.");
    }

    @Test
    void deleteAllTasksTest() {
        taskManager.addTask(task);
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        taskManager.addTask(subtask1);
        subtask2 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        taskManager.addTask(subtask2);

        taskManager.deleteAllTask();
        assertTrue(taskManager.getTaskList().isEmpty(), "Некорректное удаление задач.");

        taskManager.deleteAllSubtask();
        assertTrue(taskManager.getSubTaskList().isEmpty(), "Некорректное удаление подзадач.");
        assertTrue(epic.getListIdOfSubtasks().isEmpty(), "В Эпике есть список удаленных подзадач.");

        taskManager.deleteAllEpic();
        assertTrue(taskManager.getEpicList().isEmpty(), "Некорректное удаление эпика.");
    }

    @Test
    void reduceEpicStatusIfAllSubtasksNew() {
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.NEW, epicId);
        subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.NEW, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.NEW, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicStatusIfAllSubtasksDone() {
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.DONE, epicId);
        subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.DONE, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.DONE, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicStatusIfAllSubtasksInProgress() {
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.IN_PROGRESS, epicId);
        subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.IN_PROGRESS, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicStatusIfSubtasksNewAndDone() {
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                Status.NEW, epicId);
        subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                Status.DONE, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Status of Epic is not correct.");
    }

    @Test
    void reduceEpicTime() {
        LocalDateTime start = LocalDateTime.now();
        final int epicId = taskManager.addTask(epic);
        subtask1 = new Subtask("Test addNewSubTask1", "Test addNewTask description",
                start, 30, Status.NEW, epicId);
        subtask2 = new Subtask("Test addNewSubTask2", "Test addNewTask description",
                start.plusHours(1), 30, Status.NEW, epicId);
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        assertEquals(start, taskManager.getEpicByID(epicId).getStartTime(), "StartTime of Epic is not correct");
        assertEquals(subtask2.getEndTime(), taskManager.getEpicByID(epicId).getEndTime(), "EndTime of Epic is not correct");

        Duration allSubTaskDuration = subtask1.getDuration().plus(subtask2.getDuration());
        assertEquals(allSubTaskDuration, taskManager.getEpicByID(epicId).getDuration(), "Duration of Epic is not correct");
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