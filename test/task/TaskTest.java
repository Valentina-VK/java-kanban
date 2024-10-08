package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void tasksEqualIfSameId() {
        Task task1 = new Task("Test newTask1", "Test newTask1 description");
        Task task2 = new Task("Test newTask2", "Test newTask2 description");
        int testID = 555;
        task1.setId(testID);
        task2.setId(testID);

        Assertions.assertEquals(task1, task2, "Задачи с одним ID не равны");
        task2.setId(testID + 1);
        Assertions.assertNotEquals(task1, task2, "Задачи с разными ID равны");
    }
}
