package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubtaskTest {
    int testIDofEpic = 1001;
    Subtask subtask1 = new Subtask("Test newTask1", "Test description1", Status.NEW, testIDofEpic);
    Subtask subtask2 = new Subtask("Test newTask2", "Test description2", Status.NEW, testIDofEpic);
    int testID = 1001;

    @Test
    void SubtasksEqualIfSameId() {
        subtask1.setId(testID);
        subtask2.setId(testID);
        Assertions.assertEquals(subtask1, subtask2, "Задачи с одним ID не равны");
        subtask2.setId(testID + 1);
        Assertions.assertNotEquals(subtask1, subtask2, "Задачи с разными ID равны");
    }

    @Test
    void notSubtaskAddAsEpicToItself() {
        subtask1.setId(testIDofEpic);
        Assertions.assertNotEquals(subtask1.getId(), subtask1.getIdOfEpic(), "ID подзадачи и Эпика равны");
    }
}