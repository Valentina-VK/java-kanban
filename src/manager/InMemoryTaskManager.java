package manager;

import history.HistoryManager;
import task.Task;
import task.Epic;
import task.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskList = new HashMap<>();
    private final Map<Integer, Epic> epicList = new HashMap<>();
    private final Map<Integer, Subtask> subtaskList = new HashMap<>();
    private int countID = 0;
    private final HistoryManager history = Managers.getDefaultHistory();

    @Override
    public int addTask(Task task) {
        countID++;
        task.setId(countID);
        taskList.put(countID, task);
        return task.getId();
    }

    @Override
    public int addTask(Epic task) {
        countID++;
        task.setId(countID);
        epicList.put(countID, task);
        return task.getId();
    }

    @Override
    public int addTask(Subtask task) {
        if (!epicList.containsKey(task.getIdOfEpic())) return -1;
        countID++;
        task.setId(countID);
        subtaskList.put(countID, task);
        epicList.get(task.getIdOfEpic()).addSubtask(countID);
        deduceEpicStatus(task.getIdOfEpic());
        return task.getId();
    }

    @Override
    public void updateTask(Epic task) {
        if (epicList.containsKey(task.getId())) {
            epicList.put(task.getId(), task);
        }
    }

    @Override
    public void updateTask(Subtask task) {
        if (subtaskList.containsKey(task.getId())) {
            subtaskList.put(task.getId(), task);
            deduceEpicStatus(task.getIdOfEpic());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        }
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<Subtask> getSubTaskList() {
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public Task getTaskByID(int idOfTask) {
        if (taskList.containsKey(idOfTask)) {
            history.addHistory(taskList.get(idOfTask));
            return taskList.get(idOfTask);
        } else if (epicList.containsKey(idOfTask)) {
            history.addHistory(epicList.get(idOfTask));
            return epicList.get(idOfTask);
        } else if (subtaskList.containsKey(idOfTask)) {
            history.addHistory(subtaskList.get(idOfTask));
            return subtaskList.get(idOfTask);
        } else {
            return null;
        }
    }

    @Override
    public List<Subtask> getSubTaskList(int idOfEpic) {
        List<Subtask> subtasks = new ArrayList<>();
        List<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        for (int idSub : listIdOfSubtasks) {
            subtasks.add(subtaskList.get(idSub));
        }
        return subtasks;
    }

    @Override
    public void deleteAllTask() {
        for (Task task : taskList.values()) {
            history.remove(task.getId());
        }
        taskList.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : epicList.values()) {
            history.remove(epic.getId());
        }
        epicList.clear();
        for (Subtask subtask : subtaskList.values()) {
            history.remove(subtask.getId());
        }
        subtaskList.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtaskList.values()) {
            history.remove(subtask.getId());
        }
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubtasks();
            deduceEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteTaskById(int idOfTask) {
        if (taskList.containsKey(idOfTask)) {
            taskList.remove(idOfTask);
        } else if (epicList.containsKey(idOfTask)) {
            for (int idSubtask : epicList.get(idOfTask).getListIdOfSubtasks()) {
                subtaskList.remove(idSubtask);
                history.remove(idSubtask);
            }
            epicList.remove(idOfTask);
        } else if (subtaskList.containsKey(idOfTask)) {
            int idOfEpic = subtaskList.get(idOfTask).getIdOfEpic();
            subtaskList.remove(idOfTask);
            epicList.get(idOfEpic).deleteSubtask(idOfTask);
            deduceEpicStatus(idOfEpic);
        }
        history.remove(idOfTask);
    }


    private void deduceEpicStatus(int idOfEpic) {
        List<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
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

    public List<Task> getHistory() {
        return history.getHistory();
    }
}