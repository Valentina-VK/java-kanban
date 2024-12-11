package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT),0);
        setEndPoints();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    private void setEndPoints() {
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();

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

            HttpTaskServer server = new HttpTaskServer(taskManager);

            server.start();
        System.out.println("Сервер запущен");
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}