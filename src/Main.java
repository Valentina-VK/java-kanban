import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        System.out.println("Тестируем создание и получение списка задач!");

        taskManager.addTask(new Task("Task1", "Description of task1"));
        taskManager.addTask(new Task("Task2", "Description of task2",
                LocalDateTime.of(2024, 12, 28, 12, 0), 90));
        int idOfEpic1 = taskManager.addTask(new Epic("Epic1", "Description of Epic1"));
        taskManager.addTask(new Subtask("SubTask1 of Epic1", "Description of Subtask1",
                LocalDateTime.of(2024, 12, 30, 15, 15), 15, Status.NEW, idOfEpic1));
        taskManager.addTask(new Subtask("SubTask2 of Epic1", "Description of Subtask2",
                LocalDateTime.of(2024, 12, 30, 15, 35), 15, Status.NEW, idOfEpic1));
        taskManager.addTask(new Subtask("SubTask3 of Epic1", "Description of Subtask3",
                LocalDateTime.of(2024, 12, 30, 15, 55), 15, Status.NEW, idOfEpic1));
        taskManager.addTask(new Subtask("SubTask4 of Epic1", "Description of Subtask4",
                Status.NEW, idOfEpic1));
        int idOfEpic2 = taskManager.addTask(new Epic("Epic2", "Description of Epic2"));
        taskManager.addTask(new Subtask("SubTask1 of Epic2", "Description of Subtask1",
                Status.NEW, idOfEpic2));
        taskManager.addTask(new Subtask("SubTask2 of Epic2", "Description of Subtask2",
                Status.NEW, idOfEpic2));

        printAllTasks(taskManager);

        System.out.println("\nТестируем обновление задач и загрузку данных в Новый менеджер");
        Task oneTask = taskManager.getTaskByID(2);
        oneTask.setStatus(Status.IN_PROGRESS);
        taskManager.deleteTaskById(4);
        oneTask = taskManager.getTaskByID(9);
        oneTask.setStatus(Status.DONE);
        taskManager.updateTask((Subtask) oneTask);
        TaskManager newTaskManager = Managers.getDefault();
        printAllTasks(newTaskManager);
        System.out.println("\nТестируем приоритет задач");
        taskManager.getPrioritizedTasks()
                .forEach(System.out::println);

        System.out.println("\nТестируем удаление всех задач из файла");
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        printAllTasks(taskManager);
        taskManager.getPrioritizedTasks()
                .forEach(System.out::println);
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        taskManager.getTaskList()
                .forEach(System.out::println);
        System.out.println("Эпики:");
        taskManager.getEpicList()
                .forEach(System.out::println);
        System.out.println("Подзадачи:");
        taskManager.getSubTaskList()
                .forEach(System.out::println);
        System.out.println("История:");
        taskManager.getHistory()
                .forEach(System.out::println);
    }
}