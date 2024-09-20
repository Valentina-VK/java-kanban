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

    public void createTask(int idOfEpic, Subtask task) {
        if (!epicList.containsKey(idOfEpic)) return;
        countID++;
        task.setId(countID);
        task.setIdOfEpic(idOfEpic);
        subtaskList.put(countID, task);
        epicList.get(idOfEpic).addSubtask(countID);
    }

    public void updateTask(Epic task) {
        epicList.put(task.getId(), task);
    }

    public void updateTask(Subtask task) {
        subtaskList.put(task.getId(), task);
        int idOfEpic = task.getIdOfEpic();
        ArrayList<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();

        int countOfDone = 0;
        for (int idSub : listIdOfSubtasks) {
            if (subtaskList.get(idSub).getStatus() == STATUS.IN_PROGRESS) {
                epicList.get(idOfEpic).setStatus(STATUS.IN_PROGRESS);
                return;
            } else if (subtaskList.get(idSub).getStatus() == STATUS.DONE) {
                countOfDone++;
            }
            if (countOfDone == listIdOfSubtasks.size()) {
                epicList.get(idOfEpic).setStatus(STATUS.DONE);
            } else if (countOfDone > 0) {
                epicList.get(idOfEpic).setStatus(STATUS.IN_PROGRESS);
            }
        }
    }

    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getSubTaskList() {
        return subtaskList;
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

    public HashMap<Integer, Subtask> getSubTaskList(int idOfEpic) {
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        ArrayList<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        for (int idSub : listIdOfSubtasks) {
            subtasks.put(idSub, subtaskList.get(idSub));
        }
        return subtasks;
    }

    public void deleteAllTask() {
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
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
            subtaskList.remove(idOfTask);
        }
    }
}
