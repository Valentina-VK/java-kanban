package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private Path testPath;

    private void createTestFile() {
        try {
            testPath = Files.createTempFile("TestTaskStorage", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileBackedTaskManager createTestManager() {
        createTestFile();
        return FileBackedTaskManager.loadFromFile(testPath);
    }

    @Test
    void loadEmptyFile() {
        assertTrue(taskManager.getTasks().isEmpty(), "Map of task is not empty");
        assertTrue(taskManager.getEpics().isEmpty(), "Map of Epics is not empty");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Map of Subtasks is not empty");
    }

    @Test
    void saveEmptyFile() {
        taskManager.deleteAllTask();
        String testContent;
        try {
            testContent = Files.readString(testPath);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения тестового файла");
        }
        assertEquals("id,type,name,status,description,startTime,duration,epic\n", testContent,
                "First string of file is not title");
    }

    @Test
    void saveTasksInFile() {
        int id1 = taskManager.addTask(new Task("Task", "For test save-function 1", LocalDateTime.now(), 15));
        int id2 = taskManager.addTask(new Epic("Epic", "For test save-function 2"));
        int id3 = taskManager.addTask(new Subtask("Subtask", "For test save-function 3",
                Status.NEW, id2));
        String[] linesOfFile;
        try {
            linesOfFile = Files.readString(testPath).split("\n");
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения тестового файла");
        }
        assertEquals("id,type,name,status,description,startTime,duration,epic", linesOfFile[0],
                "First string of file is not title");
        assertEquals(taskManager.getTaskByID(id1).toString(), linesOfFile[1],
                "Second string of file is not task");
        assertEquals(taskManager.getTaskByID(id2).toString(), linesOfFile[2],
                "Third string of file is not epic");
        assertEquals(taskManager.getTaskByID(id3).toString(), linesOfFile[3],
                "Fourth string of file is not subtask");
    }

    @Test
    void readTasksFromFile() {
        String[] testContent = new String[]{
                "id,type,name,status,description,startTime,duration,epic",
                "1,TASK,task,NEW,Test description,00:00_01.01.0001,0",
                "2,EPIC,epic,IN_PROGRESS,Test description,00:00_01.01.0001,0",
                "3,SUBTASK,subtask,DONE,Test description,00:00_01.01.0001,0,2",
        };
        try (Writer fileWriter = new FileWriter(testPath.toString(), StandardCharsets.UTF_8)) {
            fileWriter.write(String.join("\n", testContent));
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в тестовый файл");
        }
        taskManager = FileBackedTaskManager.loadFromFile(testPath);
        assertEquals(testContent[1], taskManager.getTaskByID(1).toString(),
                "Second string of file is not task");
        assertEquals(testContent[2], taskManager.getTaskByID(2).toString(),
                "Third string of file is not epic");
        assertEquals(testContent[3], taskManager.getTaskByID(3).toString(),
                "Fourth string of file is not subtask");
    }
}