package manager;

import history.HistoryManager;
import task.Status;
import task.Task;
import task.Epic;
import task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskList = new HashMap<>();
    private final Map<Integer, Epic> epicList = new HashMap<>();
    private final Map<Integer, Subtask> subtaskList = new HashMap<>();
    private int countID = 0;
    protected final HistoryManager history = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    final LocalDateTime NO_TIME = LocalDateTime.of(1, 1, 1, 0, 0);

    public Map<Integer, Task> getTasks() {
        return taskList;
    }

    public Map<Integer, Epic> getEpics() {
        return epicList;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtaskList;
    }

    public void setCountID(int countID) {
        this.countID = countID;
    }

    @Override
    public int addTask(Task task) {
        if (isNoOverlap(task)) {
            countID++;
            task.setId(countID);
            taskList.put(countID, task);
            addByPriority(task);
            return task.getId();
        } else {
            throw new TaskTimeOverlapException("The task overlaps in time with existing tasks");
        }
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
        if (isNoOverlap(task)) {
            countID++;
            task.setId(countID);
            subtaskList.put(countID, task);
            epicList.get(task.getIdOfEpic()).addSubtask(countID);
            deduceEpicStatus(task.getIdOfEpic());
            addByPriority(task);
            return task.getId();
        } else {
            throw new TaskTimeOverlapException("The task overlaps in time with existing tasks");
        }
    }

    @Override
    public void updateTask(Epic task) {
        if (epicList.containsKey(task.getId())) {
            epicList.put(task.getId(), task);
        }
    }

    @Override
    public void updateTask(Subtask task) {
        if (subtaskList.containsKey(task.getId()) && isNoOverlap(task)) {
            subtaskList.put(task.getId(), task);
            prioritizedTasks.remove(task);
            addByPriority(task);
            deduceEpicStatus(task.getIdOfEpic());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId()) && isNoOverlap(task)) {
            taskList.put(task.getId(), task);
            prioritizedTasks.remove(task);
            addByPriority(task);
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
        List<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        return listIdOfSubtasks.stream().map(subtaskList::get).collect(Collectors.toList());
    }

    @Override
    public void deleteAllTask() {
        taskList.values().forEach(task -> {
            history.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        taskList.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicList.values().forEach(epic -> history.remove(epic.getId()));
        epicList.clear();
        subtaskList.values().forEach(subtask -> {
            history.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtaskList.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtaskList.values().forEach(subtask -> {
            history.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtaskList.clear();
        epicList.values().forEach(epic -> {
            epic.deleteAllSubtasks();
            deduceEpicStatus(epic.getId());
        });
    }

    @Override
    public void deleteTaskById(int idOfTask) {
        if (taskList.containsKey(idOfTask)) {
            prioritizedTasks.remove(taskList.get(idOfTask));
            taskList.remove(idOfTask);
        } else if (epicList.containsKey(idOfTask)) {
            epicList.get(idOfTask).getListIdOfSubtasks().forEach(idSubtask -> {
                subtaskList.remove(idSubtask);
                prioritizedTasks.remove(subtaskList.get(idSubtask));
                history.remove(idSubtask);
            });
            epicList.remove(idOfTask);
        } else if (subtaskList.containsKey(idOfTask)) {
            Subtask subtask = subtaskList.get(idOfTask);
            int idOfEpic = subtask.getIdOfEpic();
            prioritizedTasks.remove(subtaskList.get(idOfTask));
            subtaskList.remove(idOfTask);
            epicList.get(idOfEpic).deleteSubtask(idOfTask);
            deduceEpicStatus(idOfEpic);
            subtask.deleteIdOfEpic();
        }
        history.remove(idOfTask);
    }

    private void deduceEpicStatus(int idOfEpic) {
        List<Integer> listIdOfSubtasks = epicList.get(idOfEpic).getListIdOfSubtasks();
        if (listIdOfSubtasks.isEmpty()) {
            epicList.get(idOfEpic).setStatus(Status.NEW);
            epicList.get(idOfEpic).setTimeFields(NO_TIME, NO_TIME, Duration.ofMinutes(0));
            return;
        }
        boolean allSubNew = listIdOfSubtasks.stream().map(idSub -> subtaskList.get(idSub).getStatus())
                .allMatch(status -> status == Status.NEW);
        boolean allSubDone = listIdOfSubtasks.stream().map(idSub -> subtaskList.get(idSub).getStatus())
                .allMatch(status -> status == Status.DONE);
        if (allSubNew) {
            epicList.get(idOfEpic).setStatus(Status.NEW);
        } else if (allSubDone) {
            epicList.get(idOfEpic).setStatus(Status.DONE);
        } else {
            epicList.get(idOfEpic).setStatus(Status.IN_PROGRESS);
        }
        LocalDateTime startTime = listIdOfSubtasks.stream().map(idSub -> subtaskList.get(idSub).getStartTime())
                .filter(time -> !time.isEqual(NO_TIME)).min(LocalDateTime::compareTo).orElse(NO_TIME);
        LocalDateTime endTime = listIdOfSubtasks.stream().map(idSub -> subtaskList.get(idSub).getEndTime())
                .max(LocalDateTime::compareTo).orElse(NO_TIME);
        Duration duration = listIdOfSubtasks.stream().map(idSub -> subtaskList.get(idSub).getDuration())
                .reduce(Duration.ZERO, Duration::plus);
        epicList.get(idOfEpic).setTimeFields(startTime, endTime, duration);
    }

    private boolean isNoOverlap(Task newTask) {
        LocalDateTime start = newTask.getStartTime();
        LocalDateTime end = newTask.getEndTime();
        if (start.isEqual(NO_TIME)) return true;
        return prioritizedTasks.stream().allMatch(task ->
                task.getStartTime().isAfter(end) || task.getStartTime().isEqual(end) ||
                        task.getEndTime().isBefore(start) || task.getEndTime().isEqual(start));
    }

    protected void addByPriority(Task task) {
        if (!task.getStartTime().isEqual(NO_TIME)) prioritizedTasks.add(task);
    }

    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }
}