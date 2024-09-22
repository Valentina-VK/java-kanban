package manager;

import task.Task;
import task.Epic;
import task.Subtask;
import history.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private int countID = 0;


    public void createTask(Task task) {
        countID++;
        task.setId(countID);
        taskList.put(countID, task);
    }

    public void createTask(Epic task) {
        countID++;
        task.setId(countID);
        epicList.put(countID, task);
    }

    public void createTask(Subtask task) {
        if (!epicList.containsKey(task.getIdOfEpic())) return;
        countID++;
        task.setId(countID);
        task.setIdOfEpic(task.getIdOfEpic());
        subtaskList.put(countID, task);
        epicList.get(task.getIdOfEpic()).addSubtask(countID);
    }

    public void updateTask(Epic task) {
        epicList.put(task.getId(), task);
    }

    public void updateTask(Subtask task) {
        subtaskList.put(task.getId(), task);
        deduceEpicStatus(task.getIdOfEpic());
    }

    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicList.values());
    }

    public ArrayList<Subtask> getSubTaskList() {
        return new ArrayList<>(subtaskList.values());
    }

    public Task getTaskByID(int idOfTask) {
        if (taskList.containsKey(idOfTask)) {
            return taskList.get(idOfTask);
        } else if (epicList.containsKey(idOfTask)) {
            return epicList.get(idOfTask);
        } else {
            return subtaskList.get(idOfTask);
        }
    }

    public ArrayList<Subtask> getSubTaskList(int idOfEpic) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayList<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        for (int idSub : listIdOfSubtasks) {
            subtasks.add(subtaskList.get(idSub));
        }
        return subtasks;
    }

    public void deleteAllTask() {
        taskList.clear();
    }

    public void deleteAllEpic() {
        epicList.clear();
        subtaskList.clear();
    }

    public void deleteAllSubtask() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubtasks();
            deduceEpicStatus(epic.getId());
        }
    }

    public void deleteAllSubtask(int idOfEpic) {
        ArrayList<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        for (int idSubtask : listIdOfSubtasks) {
            subtaskList.remove(idSubtask);
        }
        epicList.get(idOfEpic).deleteAllSubtasks();
        deduceEpicStatus(idOfEpic);
    }

    public void deleteTaskById(int idOfTask) {
        if (taskList.containsKey(idOfTask)) {
            taskList.remove(idOfTask);
        } else if (epicList.containsKey(idOfTask)) {
            ArrayList<Integer> listIdOfSubtasks = epicList.get(idOfTask).getListIdOfSubtasks();
            for (int idSubtask : listIdOfSubtasks) {
                subtaskList.remove(idSubtask);
            }
            epicList.remove(idOfTask);
        } else {
            int idOfEpic = subtaskList.get(idOfTask).getIdOfEpic();
            subtaskList.remove(idOfTask);
            epicList.get(idOfEpic).deleteSubtask(idOfTask);
            deduceEpicStatus(idOfEpic);
        }
    }

    void deduceEpicStatus(int idOfEpic) {
        ArrayList<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        int countOfDone = 0;
        if (listIdOfSubtasks.isEmpty()) epicList.get(idOfEpic).setStatus(Status.NEW);
        for (int idSub : listIdOfSubtasks) {
            if (subtaskList.get(idSub).getStatus() == Status.IN_PROGRESS) {
                epicList.get(idOfEpic).setStatus(Status.IN_PROGRESS);
                return;
            } else if (subtaskList.get(idSub).getStatus() == Status.DONE) {
                countOfDone++;
            }
            if (countOfDone == listIdOfSubtasks.size()) {
                epicList.get(idOfEpic).setStatus(Status.DONE);
            } else if (countOfDone > 0) {
                epicList.get(idOfEpic).setStatus(Status.IN_PROGRESS);
            } else {
                epicList.get(idOfEpic).setStatus(Status.DONE);
            }
        }
    }
}

