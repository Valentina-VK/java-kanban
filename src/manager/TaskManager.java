package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    int addTask(Task task);

    int addTask(Epic task);

    int addTask(Subtask task);

    void updateTask(Epic task);

    void updateTask(Subtask task);

    void updateTask(Task task);

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubTaskList();

    Task getTaskByID(int idOfTask);

    Subtask getSubtaskByID(int idOfTask);

    Epic getEpicByID(int idOfTask);

    List<Subtask> getSubTaskList(int idOfEpic);

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    void deleteTaskById(int idOfTask);

    void deleteSubtaskById(int idOfTask);

    void deleteEpicById(int idOfTask);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}