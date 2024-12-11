package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import task.Epic;
import task.Status;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubtasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    LocalDateTime startTime = LocalDateTime.now();
    Epic epic;

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        epic = new Epic("TestEpic1", "Testing task 1");
        manager.addTask(epic);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getSubTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TestSubtask1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        manager.addTask(task);
        Subtask updatedTask = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.DONE, epic.getId());
        updatedTask.setId(task.getId());
        String taskJson = gson.toJson(updatedTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(Status.DONE, manager.getSubtaskByID(task.getId()).getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void testAddOverlapSubtask() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        manager.addTask(task);
        Subtask overlapTask = new Subtask("TestSubtask2", "Testing task 2", startTime, 60, Status.DONE, epic.getId());
        String taskJson = gson.toJson(overlapTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getSubTaskList().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllSubtasks() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        Subtask task2 = new Subtask("TestSubtask2", "Testing task 2", startTime.plusHours(2), 60, Status.DONE, epic.getId());
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubTaskList().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        Subtask task2 = new Subtask("TestSubtask2", "Testing task 2", startTime.plusHours(2), 60, Status.DONE, epic.getId());
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getSubTaskList().size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        Subtask task2 = new Subtask("TestSubtask2", "Testing task 2", startTime.plusHours(2), 60, Status.DONE, epic.getId());
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> serverTasks = gson.fromJson(response.body(),
                new TypeToken<List<Subtask>>() {
                }.getType());
        assertEquals(task, serverTasks.get(0), "Задачи не совпадают");
        assertEquals(task2, serverTasks.get(1), "Задачи не совпадают");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        Subtask task2 = new Subtask("TestSubtask2", "Testing task 2", startTime.plusHours(2), 60, Status.DONE, epic.getId());
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + task2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask serverTask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(task2, serverTask, "Задачи не совпадают");
    }

    @Test
    public void testGetSubtaskByBadId() throws IOException, InterruptedException {
        Subtask task = new Subtask("TestSubtask1", "Testing task 1", startTime, 60, Status.NEW, epic.getId());
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/777");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}