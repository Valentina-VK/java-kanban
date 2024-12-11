package server;

import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TaskTimeOverlapException;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    getByRequest(httpExchange);
                    break;
                }
                case "POST": {
                    postByRequest(httpExchange);
                    break;
                }
                case "DELETE": {
                    deleteByRequest(httpExchange);
                    break;
                }
                default:
                    System.out.println("Необрабатываемый метод запроса");
                    sendMethodNotAllowed(httpExchange);
            }
        } catch (NotFoundException exception) {
            System.out.println(exception.getMessage());
            sendNotFound(httpExchange);
        } catch (TaskTimeOverlapException exception) {
            System.out.println(exception.getMessage());
            sendHasInteractions(httpExchange);
        } catch (Exception exception) {
            httpExchange.sendResponseHeaders(500, 0);
            httpExchange.close();
        }
    }

    private void getByRequest(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/tasks$", path)) {
            String response = gson.toJson(manager.getTaskList());
            sendText(httpExchange, response);
            return;
        }
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getTaskByID(id));
                sendText(httpExchange, response);
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    private void postByRequest(HttpExchange httpExchange) throws IOException, TaskTimeOverlapException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        Task task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Task.class);
        if (Pattern.matches("^/tasks$", path)) {
            if (task.getId() > 0) manager.updateTask(task);
            else manager.addTask(task);
            httpExchange.sendResponseHeaders(201, 0);
            httpExchange.close();
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    private void deleteByRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/tasks$", path)) {
            manager.deleteAllTask();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.close();
            return;
        }
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteTaskById(id);
                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.close();
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}