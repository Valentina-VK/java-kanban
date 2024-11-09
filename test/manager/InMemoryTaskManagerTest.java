package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class InMemoryTaskManagerTest {
    private static final TaskManager taskManager = Managers.getDefault();

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
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
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addTask(epic);
        final Epic savedEpic = (Epic) taskManager.getTaskByID(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        savedEpic.addSubtask(epicId);
        final List<Integer> subtasksOfEpic = savedEpic.getListIdOfSubtasks();

        assertNotNull(subtasksOfEpic, "Задачи не возвращаются.");
        assertEquals(0, subtasksOfEpic.size(), "Неверное количество подзадач.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        final int subId1 = taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, subId1);
        final int subId2 = taskManager.addTask(subtask2);

        final Subtask savedSub = (Subtask) taskManager.getTaskByID(subId1);

        assertNotNull(savedSub, "Задача не найдена.");
        assertEquals(subtask1, savedSub, "Задачи не совпадают.");

        assertNull(taskManager.getTaskByID(subId2), "Некорректное сохранение позадачи.");

        final List<Subtask> subtasks = taskManager.getSubTaskList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void notConflictTasksInMemoryTaskManager() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        int notGenId = 1111111;
        task.setId(notGenId);
        taskManager.updateTask(task);
        assertNull(taskManager.getTaskByID(notGenId), "Некорректное обновление позадачи.");

        final List<Task> tasks = taskManager.getTaskList();
        int savedSize = tasks.size();
        taskManager.deleteTaskById(notGenId);
        assertEquals(savedSize, taskManager.getTaskList().size(), "Неверное количество задач.");

        final int newTaskId = taskManager.addTask(task);
        assertNotEquals(newTaskId, notGenId, "Новый ID при добавлении не сгенерирован");
    }

    @Test
    void deleteTaskTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task);
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        final int subId1 = taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        final int subId2 = taskManager.addTask(subtask2);

        taskManager.deleteTaskById(taskId);
        assertNull(taskManager.getTaskByID(taskId), "Некорректное удаление задачи.");

        taskManager.deleteTaskById(subId1);
        assertNull(taskManager.getTaskByID(subId1), "Некорректное удаление подзадачи.");
        epic = (Epic) taskManager.getTaskByID(epicId);
        assertFalse(epic.getListIdOfSubtasks().contains(subId1), "В Эпике есть ссылка на удаленную подзадачу.");

        taskManager.deleteTaskById(epicId);
        assertNull(taskManager.getTaskByID(epicId), "Некорректное удаление эпика.");
        assertNull(taskManager.getTaskByID(subId2), "При удалении эпика осталась подзадача.");
    }

    @Test
    void deleteAllTasksTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addTask(epic);
        Subtask subtask1 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask("Test addNewTask", "Test addNewTask description",
                Status.NEW, epicId);
        taskManager.addTask(subtask2);

        taskManager.deleteAllTask();
        assertTrue(taskManager.getTaskList().isEmpty(), "Некорректное удаление задач.");

        taskManager.deleteAllSubtask();
        assertTrue(taskManager.getSubTaskList().isEmpty(), "Некорректное удаление подзадач.");
        epic = (Epic) taskManager.getTaskByID(epicId);
        assertTrue(epic.getListIdOfSubtasks().isEmpty(), "В Эпике есть список удаленных подзадач.");

        taskManager.deleteAllEpic();
        assertTrue(taskManager.getEpicList().isEmpty(), "Некорректное удаление эпика.");
    }
}