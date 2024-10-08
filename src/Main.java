import manager.Managers;
import manager.Status;
import manager.TaskManager;
import task.Task;
import task.Epic;
import task.Subtask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager taskManager = Managers.getDefault();

        System.out.println("Тестируем создание и получение списка задач!");

        taskManager.addTask(new Task("Убока", "Вымыть полы"));
        taskManager.addTask(new Task("Стирка", "Постирать джинсы"));
        int idOfEpic1 = taskManager.addTask(new Epic("Сходить в магазин", "Купить продукты к обеду по списку"));
        taskManager.addTask(new Subtask("Купить хлеб", "Цельнозерновой, нарезной",
                Status.NEW, idOfEpic1));
        taskManager.addTask(new Subtask("Купить лук", "белый, две головки", Status.NEW, idOfEpic1));
        taskManager.addTask(new Subtask("Купить моковь", "2 небольшие или 1 крупную",
                Status.NEW, idOfEpic1));
        taskManager.addTask(new Subtask("Купить сметану", "Свежая, жирность 20%, 300гр",
                Status.NEW, idOfEpic1));
        int idOfEpic2 = taskManager.addTask(new Epic("Обучение", "3-4 часа заниматься изучением Java"));
        taskManager.addTask(new Subtask("Изучить теорию", "очередная новая тема",
                Status.NEW, idOfEpic2));
        taskManager.addTask(new Subtask("Выполнить практические задания",
                "внимательно читаем задания", Status.NEW, idOfEpic2));

        printAllTasks(taskManager);

        System.out.println("\nТестируем обновление и получение задачи по ID!");
        Task oneTask = taskManager.getTaskByID(2);
        oneTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(oneTask);
        System.out.println(oneTask);
        System.out.println("\nТестируем обновление статуса Эпика при обновлении подзадачи");
        oneTask = taskManager.getTaskByID(9);
        oneTask.setStatus(Status.DONE);
        taskManager.updateTask((Subtask) oneTask);
        oneTask = taskManager.getTaskByID(idOfEpic2);
        System.out.println(oneTask);
        System.out.println("\nТестируем получение списка всех подзадач Эпика");
        for (Task task : taskManager.getSubTaskList(idOfEpic2)) {
            System.out.println(task);
        }
        System.out.println("\nТестируем удаление задач по ID");
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(idOfEpic1);
        printAllTasks(taskManager);
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
        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task task : taskManager.getEpicList()) {
            System.out.println(task);
        }
        System.out.println("Подзадачи:");
        for (Task task : taskManager.getSubTaskList()) {
            System.out.println(task);
        }
        System.out.println("История:");
        for (Task task : Managers.getDefaultHistory().getHistory()) {
            System.out.println(task);
        }
    }


}