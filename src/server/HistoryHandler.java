package server;

import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TaskTimeOverlapException;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            try {
                String requestMethod = httpExchange.getRequestMethod();
                if (requestMethod.equals("GET")) {
                    getByRequest(httpExchange);
                } else {
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
        if (Pattern.matches("^/history$", path)) {
            String response = gson.toJson(manager.getHistory());
            sendText(httpExchange, response);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}