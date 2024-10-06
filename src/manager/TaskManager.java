package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {
    int addTask(Task task);

    int addTask(Epic task);

    int addTask(Subtask task);

    void updateTask(Epic task);

    void updateTask(Subtask task);

    void updateTask(Task task);

    ArrayList<Task> getTaskList();

    ArrayList<Epic> getEpicList();

    ArrayList<Subtask> getSubTaskList();

    Task getTaskByID(int idOfTask);

    ArrayList<Subtask> getSubTaskList(int idOfEpic);

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    void deleteTaskById(int idOfTask);
}
