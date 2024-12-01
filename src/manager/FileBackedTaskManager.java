package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import task.Type;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static manager.DateTimeFormat.DATE_TIME_FORMAT;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String TITLE = "id,type,name,status,description,startTime,duration,epic";
    private final Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
        if (Files.notExists(path)) {
            createTasksStorage(path);
        }
    }

    private void createTasksStorage(Path path) {
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.createFile(path);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка создания файла для сохранения: " + exception.getMessage());
        }
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(path.toString(), StandardCharsets.UTF_8)) {
            fileWriter.write(TITLE + "\n");
            for (Task task : getTasks().values()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Task task : getEpics().values()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Task task : getSubtasks().values()) {
                fileWriter.write(task.toString() + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка автосохранения: " + exception.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(path);
        int countID = -1;
        String content;
        try {
            content = Files.readString(path);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        if (content.length() < TITLE.length()) return taskManager;
        String[] lines = content.substring(TITLE.length() + 1).split("\n");
        if (!lines[0].isEmpty()) {
            for (String line : lines) {
                Task task = fromString(line);
                if (countID < task.getId()) {
                    countID = task.getId();
                }
                switch (task.getType()) {
                    case TASK:
                        taskManager.getTasks().put(task.getId(), task);
                        taskManager.addByPriority(task);
                        break;
                    case EPIC:
                        taskManager.getEpics().put(task.getId(), (Epic) task);
                        break;
                    default:
                        taskManager.getSubtasks().put(task.getId(), (Subtask) task);
                        taskManager.addByPriority(task);
                }
            }
        }
        taskManager.setCountID(++countID);
        return taskManager;
    }

    private static Task fromString(String value) {
        String[] fieldsOfTask = value.split(",");
        int id = Integer.parseInt(fieldsOfTask[0]);
        Type type = Type.valueOf(fieldsOfTask[1]);
        Status status = Status.valueOf(fieldsOfTask[3]);
        LocalDateTime startTime = LocalDateTime.parse(fieldsOfTask[5], DATE_TIME_FORMAT);
        int durationInMinutes = Integer.parseInt(fieldsOfTask[6]);
        switch (type) {
            case TASK:
                Task task = new Task(fieldsOfTask[2], fieldsOfTask[4], startTime, durationInMinutes);
                task.setId(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(fieldsOfTask[2], fieldsOfTask[4]);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            default:
                Subtask subtask = new Subtask(fieldsOfTask[2], fieldsOfTask[4], startTime, durationInMinutes,
                        status, Integer.parseInt(fieldsOfTask[7]));
                subtask.setId(id);
                return subtask;
        }
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addTask(Epic task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addTask(Subtask task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public void updateTask(Epic task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateTask(Subtask task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();

    }

    @Override
    public void deleteTaskById(int idOfTask) {
        super.deleteTaskById(idOfTask);
        save();
    }
}