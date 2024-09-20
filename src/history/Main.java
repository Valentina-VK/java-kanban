package history;

import manager.TaskManager;
import task.Task;
import task.Epic;
import task.Subtask;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        System.out.println("Тестируем создание и получение списка задач!");

        taskManager.createTask(new Task("Убока", "Вымыть полы"));
        taskManager.createTask(new Task("Стирка", "Постирать джинсы"));
        taskManager.createTask(new Epic("Сходить в магазин", "Купить продукты к обеду по списку"));
        taskManager.createTask(3, new Subtask("Купить хлеб", "Цельнозерновой, нарезной"));
        taskManager.createTask(3, new Subtask("Купить лук", "белый, две головки"));
        taskManager.createTask(3, new Subtask("Купить моковь", "2 небольшие или 1 крупную"));
        taskManager.createTask(3, new Subtask("Купить сметану", "Свежая, жирность 20%, 300гр"));
        taskManager.createTask(new Epic("Обучение", "3-4 часа заниматься изучением Java"));
        taskManager.createTask(8, new Subtask("Изучить теорию", "очередная новая тема"));
        taskManager.createTask(8, new Subtask("Выполнить практические задания",
                "внимательно читаем задания"));
        HashMap<Integer, Task> list = taskManager.getTaskList();
        for (Task task : list.values()) {
            System.out.println(task);
        }
        HashMap<Integer, Epic> listEpic = taskManager.getEpicList();
        for (Task task : listEpic.values()) {
            System.out.println(task);
        }
        HashMap<Integer, Subtask> listSubtask = taskManager.getSubTaskList();
        for (Task task : listSubtask.values()) {
            System.out.println(task);
        }
        System.out.println("\nТестируем обновление и получение задачи по ID!");
        Task oneTask = taskManager.getTaskByID(2);
        oneTask.setStatus(STATUS.IN_PROGRESS);
        taskManager.updateTask(oneTask);
        System.out.println(oneTask);
        System.out.println("\nТестируем обновление статуса Эпика при обновлении подзадачи");
        oneTask = taskManager.getTaskByID(9);
        oneTask.setStatus(STATUS.DONE);
        taskManager.updateTask((Subtask) oneTask);
        oneTask = taskManager.getTaskByID(8);
        System.out.println(oneTask);
        System.out.println("\nТестируем получение списка всех подзадач Эпика");
        listSubtask = taskManager.getSubTaskList(8);
        for (Task task : listSubtask.values()) {
            System.out.println(task);
        }
        System.out.println("\nТестируем удаление задач по ID");
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(3);
        for (Task task : list.values()) {
            System.out.println(task);
        }
        for (Task task : listEpic.values()) {
            System.out.println(task);
        }
        listSubtask = taskManager.getSubTaskList();
        for (Task task : listSubtask.values()) {
            System.out.println(task);
        }
        System.out.println("\nТестируем удаление всех задач");
        taskManager.deleteAllTask();
        for (Task task : list.values()) {
            System.out.println(task);
        }
        for (Task task : listEpic.values()) {
            System.out.println(task);
        }
        for (Task task : listSubtask.values()) {
            System.out.println(task);
        }


    }

}