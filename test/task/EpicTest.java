package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {
    Epic epic1 = new Epic("Test newTask1", "Test newTask1 description");
    Epic epic2 = new Epic("Test newTask2", "Test newTask2 description");
    int testID = 1001;

    @Test
    void epicsEqualIfSameId() {
        epic1.setId(testID);
        epic2.setId(testID);
        Assertions.assertEquals(epic1, epic2, "Задачи с одним ID не равны");
        epic2.setId(testID + 1);
        Assertions.assertNotEquals(epic1, epic2, "Задачи с разными ID равны");
    }

    @Test
    void notAddEpicAsSubtaskToItself() {
        epic1.addSubtask(epic1.getId());
        Assertions.assertTrue(epic1.getListIdOfSubtasks().isEmpty());
    }
}