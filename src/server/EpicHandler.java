package server;

import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TaskTimeOverlapException;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
        if (Pattern.matches("^/epics$", path)) {
            String response = gson.toJson(manager.getEpicList());
            sendText(httpExchange, response);
            return;
        }
        if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getEpicByID(id));
                sendText(httpExchange, response);
            }
        }
        if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
            String pathId = path.replaceFirst("/epics/", "").replaceFirst("/subtasks", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getSubTaskList(id));
                sendText(httpExchange, response);
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    private void postByRequest(HttpExchange httpExchange) throws IOException, TaskTimeOverlapException, NotFoundException {
        String path = httpExchange.getRequestURI().getPath();
        Epic task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Epic.class);
        if (Pattern.matches("^/epics$", path)) {
            if (task.getId() > 0) manager.updateTask(task);
            else manager.addTask(task);
            httpExchange.sendResponseHeaders(CodeResponse.MODIFIED.getCode(), 0);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }

    private void deleteByRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/epics$", path)) {
            manager.deleteAllEpic();
            httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), 0);
            return;
        }
        if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteEpicById(id);
                httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), 0);
            }
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}