package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TaskTimeOverlapException;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager manager;

    public TaskHandler(TaskManager manager) {
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
            httpExchange.sendResponseHeaders(CodeResponse.SERVER_ERROR.getCode(), 0);
        } finally {
            httpExchange.close();
        }
    }

    protected void getByRequest(HttpExchange httpExchange) throws IOException, NotFoundException {
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

    protected void postByRequest(HttpExchange httpExchange) throws IOException, TaskTimeOverlapException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        Task task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Task.class);
        if (Pattern.matches("^/tasks$", path)) {
            if (task.getId() > 0) manager.updateTask(task);
            else manager.addTask(task);
            httpExchange.sendResponseHeaders(CodeResponse.MODIFIED.getCode(), 0);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    protected void deleteByRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/tasks$", path)) {
            manager.deleteAllTask();
            httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), 0);
            return;
        }
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteTaskById(id);
                httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), 0);
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}