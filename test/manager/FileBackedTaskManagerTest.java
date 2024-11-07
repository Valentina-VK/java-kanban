package manager;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    private Path testPath;

    @BeforeEach
    void createTestFile() {
        try {
            testPath = Files.createTempFile("TestTaskStorage", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadEmptyFile() {
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(testPath);
        assertTrue(testManager.getTasks().isEmpty(), "Map of task is not empty");
        assertTrue(testManager.getEpics().isEmpty(), "Map of Epics is not empty");
        assertTrue(testManager.getSubtasks().isEmpty(), "Map of Subtasks is not empty");
    }

    @Test
    void saveEmptyFile() {
        FileBackedTaskManager testManager = new FileBackedTaskManager(testPath);
        testManager.deleteAllTask();
        String testContent;
        try {
            testContent = Files.readString(testPath);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения тестового файла");
        }
        assertEquals("id,type,name,status,description,epic\n", testContent,
                "First string of file is not title");
    }

    @Test
    void saveTasksInFile() {
        FileBackedTaskManager testManager = new FileBackedTaskManager(testPath);
        int id1 = testManager.addTask(new Task("Task", "For test save-function 1"));
        int id2 = testManager.addTask(new Epic("Epic", "For test save-function 2"));
        int id3 = testManager.addTask(new Subtask("Subtask", "For test save-function 3",
                Status.NEW, id2));
        String[] linesOfFile;
        try {
            linesOfFile = Files.readString(testPath).split("\n");
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения тестового файла");
        }
        assertEquals("id,type,name,status,description,epic", linesOfFile[0],
                "First string of file is not title");
        assertEquals(testManager.getTaskByID(id1).toString(), linesOfFile[1],
                "Second string of file is not task");
        assertEquals(testManager.getTaskByID(id2).toString(), linesOfFile[2],
                "Third string of file is not epic");
        assertEquals(testManager.getTaskByID(id3).toString(), linesOfFile[3],
                "Fourth string of file is not subtask");
    }

    @Test
    void readTasksFromFile() {
        String[] testContent = new String[]{
                "id,type,name,status,description,epic",
                "1,TASK,task,NEW,Test description",
                "2,EPIC,epic,IN_PROGRESS,Test description",
                "3,SUBTASK,subtask,DONE,Test description,2",
        };
        try (Writer fileWriter = new FileWriter(testPath.toString(), StandardCharsets.UTF_8)) {
            fileWriter.write(String.join("\n", testContent));
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в тестовый файл");
        }
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(testPath);
        assertEquals(testContent[1], testManager.getTaskByID(1).toString(),
                "Second string of file is not task");
        assertEquals(testContent[2], testManager.getTaskByID(2).toString(),
                "Third string of file is not epic");
        assertEquals(testContent[3], testManager.getTaskByID(3).toString(),
                "Fourth string of file is not subtask");
    }
}
