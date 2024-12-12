package server;

import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TaskTimeOverlapException;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
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
            }
        }
    }

    private void getByRequest(HttpExchange httpExchange) throws IOException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/subtasks$", path)) {
            String response = gson.toJson(manager.getSubTaskList());
            sendText(httpExchange, response);
            return;
        }
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getSubtaskByID(id));
                sendText(httpExchange, response);
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    private void postByRequest(HttpExchange httpExchange) throws IOException, TaskTimeOverlapException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        Subtask task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Subtask.class);
        if (Pattern.matches("^/subtasks$", path)) {
            if (task.getId() > 0) manager.updateTask(task);
            else manager.addTask(task);
            httpExchange.sendResponseHeaders(CodeResponse.MODIFIED.getCode(), 0);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    private void deleteByRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/subtasks$", path)) {
            manager.deleteAllSubtask();
            httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), 0);
            return;
        }
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteSubtaskById(id);
                httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), 0);
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}