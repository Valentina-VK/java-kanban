package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTask();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("TestTask1", "Testing task 1");
        Task task2 = new Task("TestTask2", "Testing task 2");
        Task task3 = new Task("TestTask3", "Testing task 3");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.getTaskByID(task1.getId());
        manager.getTaskByID(task2.getId());
        manager.getTaskByID(task3.getId());
        manager.getTaskByID(task1.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> serverTasks = gson.fromJson(response.body(),
                new TypeToken<List<Task>>() {
                }.getType());
        assertEquals(3, serverTasks.size(), "Размер списка задач не совпадает");
        assertEquals(task2, serverTasks.get(0), "Задачи не совпадают");
        assertEquals(task3, serverTasks.get(1), "Задачи не совпадают");
        assertEquals(task1, serverTasks.get(2), "Задачи не совпадают");
    }

    @Test
    public void testNotAllowedMethodForHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }
}