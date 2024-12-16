package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends GenericTypeHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void getByRequest(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (Pattern.matches("^/history$", path)) {
            String response = gson.toJson(manager.getHistory());
            sendText(httpExchange, response);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}