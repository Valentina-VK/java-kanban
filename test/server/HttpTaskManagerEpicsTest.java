package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class HttpTaskManagerEpicsTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    private final LocalDateTime startTime = LocalDateTime.now();
    private Epic task;

    public HttpTaskManagerEpicsTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllEpic();
        manager.deleteAllSubtask();
        task = new Epic("TestEpic", "Testing task");
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        System.out.println(task);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CodeResponse.MODIFIED.getCode(), response.statusCode());

        List<Epic> tasksFromManager = manager.getEpicList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TestEpic", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        manager.addTask(task);
        Epic updatedTask = new Epic("UpdateEpic", "Testing task 1");
        updatedTask.setId(task.getId());
        String taskJson = gson.toJson(updatedTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.MODIFIED.getCode(), response.statusCode());
        assertEquals("UpdateEpic", manager.getEpicByID(task.getId()).getName(), "Некорректное обновление имени задачи");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic task2 = new Epic("TestEpic2", "Testing task 2");
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());
        assertEquals(0, manager.getEpicList().size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic task2 = new Epic("TestEpic2", "Testing task 2");
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());
        assertEquals(1, manager.getEpicList().size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic task2 = new Epic("TestEpic2", "Testing task 2");
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());

        List<Epic> serverTasks = gson.fromJson(response.body(),
                new TypeToken<List<Epic>>() {
                }.getType());
        assertEquals(task, serverTasks.get(0), "Задачи не совпадают");
        assertEquals(task2, serverTasks.get(1), "Задачи не совпадают");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic task2 = new Epic("TestEpic2", "Testing task 2");
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + task2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());

        Epic serverTask = gson.fromJson(response.body(), Epic.class);
        assertEquals(task2, serverTask, "Задачи не совпадают");
    }

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException {
        manager.addTask(task);
        Subtask subtask1 = new Subtask("TestSub1", "Testing task 1", startTime, 60, Status.NEW, task.getId());
        Subtask subtask2 = new Subtask("TestSub2", "Testing task 2", startTime.plusHours(2), 60, Status.NEW, task.getId());
        manager.addTask(subtask1);
        manager.addTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + task.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.OK.getCode(), response.statusCode());

        List<Subtask> serverTasks = gson.fromJson(response.body(),
                new TypeToken<List<Subtask>>() {
                }.getType());
        assertEquals(subtask1, serverTasks.get(0), "Задачи не совпадают");
        assertEquals(subtask2, serverTasks.get(1), "Задачи не совпадают");
    }

    @Test
    public void testGetEpicByBadId() throws IOException, InterruptedException {
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/777");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CodeResponse.NOT_FOUND.getCode(), response.statusCode());
    }
}