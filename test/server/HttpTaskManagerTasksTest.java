package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final LocalDateTime startTime = LocalDateTime.now();
    private Task task;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTask();
        task = new Task("TestTask1", "Testing task 1", startTime, 60);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CodeResponse.MODIFIED.getCode(), response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TestTask1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        manager.addTask(task);
        Task updatedTask = new Task("TestTask1", "Testing task 1", startTime, 60);
        updatedTask.setStatus(Status.DONE);
        updatedTask.setId(task.getId());
        String taskJson = gson.toJson(updatedTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.MODIFIED.getCode(), response.statusCode());
        assertEquals(Status.DONE, manager.getTaskByID(task.getId()).getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void testAddOverlapTask() throws IOException, InterruptedException {
        manager.addTask(task);
        Task overlapTask = new Task("TestTask2", "Testing task 2", startTime, 60);

        String taskJson = gson.toJson(overlapTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OVERLAP.getCode(), response.statusCode());
        assertEquals(1, manager.getTaskList().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        Task task2 = new Task("TestTask2", "Testing task 2", startTime.plusHours(2), 60);
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());
        assertEquals(0, manager.getTaskList().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task2 = new Task("TestTask2", "Testing task 2", startTime.plusHours(2), 60);
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());
        assertEquals(1, manager.getTaskList().size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task2 = new Task("TestTask2", "Testing task 2", startTime.plusHours(2), 60);
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());

        List<Task> serverTasks = gson.fromJson(response.body(),
                new TypeToken<List<Task>>() {
                }.getType());
        assertEquals(task, serverTasks.get(0), "Задачи не совпадают");
        assertEquals(task2, serverTasks.get(1), "Задачи не совпадают");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task2 = new Task("TestTask2", "Testing task 2", startTime.plusHours(2), 60);
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());

        Task serverTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task2, serverTask, "Задачи не совпадают");
    }

    @Test
    public void testGetTaskByBadId() throws IOException, InterruptedException {
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/777");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.NOT_FOUND.getCode(), response.statusCode());
    }
}