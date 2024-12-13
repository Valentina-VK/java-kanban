package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                getByRequest(httpExchange);
            } else {
                System.out.println("Необрабатываемый метод запроса");
                sendMethodNotAllowed(httpExchange);
            }
        } catch (Exception exception) {
            httpExchange.sendResponseHeaders(CodeResponse.SERVER_ERROR.getCode(), 0);
        } finally {
            httpExchange.close();
        }
    }

    private void getByRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/prioritized$", path)) {
            String response = gson.toJson(manager.getPrioritizedTasks());
            sendText(httpExchange, response);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}