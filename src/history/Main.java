package history;

import manager.TaskManager;
import task.Task;
import task.Epic;
import task.Subtask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        System.out.println("Тестируем создание и получение списка задач!");

        taskManager.createTask(new Task("Убока", "Вымыть полы"));
        taskManager.createTask(new Task("Стирка", "Постирать джинсы"));
        taskManager.createTask(new Epic("Сходить в магазин", "Купить продукты к обеду по списку"));
        taskManager.createTask(new Subtask("Купить хлеб", "Цельнозерновой, нарезной",
                Status.NEW, 3));
        taskManager.createTask(new Subtask("Купить лук", "белый, две головки", Status.NEW, 3));
        taskManager.createTask(new Subtask("Купить моковь", "2 небольшие или 1 крупную",
                Status.NEW, 3));
        taskManager.createTask(new Subtask("Купить сметану", "Свежая, жирность 20%, 300гр",
                Status.NEW, 3));
        taskManager.createTask(new Epic("Обучение", "3-4 часа заниматься изучением Java"));
        taskManager.createTask(new Subtask("Изучить теорию", "очередная новая тема",
                Status.NEW, 8));
        taskManager.createTask(new Subtask("Выполнить практические задания",
                "внимательно читаем задания", Status.NEW, 8));
        for (Task task : taskManager.getTaskList()) {
            System.out.println(task);
        }
        for (Task task : taskManager.getEpicList()) {
            System.out.println(task);
        }
        for (Task task : taskManager.getSubTaskList()) {
            System.out.println(task);
        }
        System.out.println("\nТестируем обновление и получение задачи по ID!");
        Task oneTask = taskManager.getTaskByID(2);
        oneTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(oneTask);
        System.out.println(oneTask);
        System.out.println("\nТестируем обновление статуса Эпика при обновлении подзадачи");
        oneTask = taskManager.getTaskByID(9);
        oneTask.setStatus(Status.DONE);
        taskManager.updateTask((Subtask) oneTask);
        oneTask = taskManager.getTaskByID(8);
        System.out.println(oneTask);
        System.out.println("\nТестируем получение списка всех подзадач Эпика");
        for (Task task : taskManager.getSubTaskList(8)) {
            System.out.println(task);
        }
        System.out.println("\nТестируем удаление задач по ID");
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(3);
        for (Task task : taskManager.getTaskList()) {
            System.out.println(task);
        }
        for (Task task : taskManager.getEpicList()) {
            System.out.println(task);
        }
        for (Task task : taskManager.getSubTaskList()) {
            System.out.println(task);
        }
        System.out.println("\nТестируем удаление всех подзадач");
        taskManager.deleteAllSubtask();
        for (Task task : taskManager.getEpicList()) {
            System.out.println(task);
        }
        for (Task task : taskManager.getSubTaskList()) {
            System.out.println(task);
        }
        System.out.println("\nТестируем удаление всех задач");
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        for (Task task : taskManager.getTaskList()) {
            System.out.println(task);
        }
        for (Task task : taskManager.getEpicList()) {
            System.out.println(task);
        }
    }

}