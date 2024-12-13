package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected final Gson gson = HttpTaskServer.getGson();

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(CodeResponse.OK.getCode(), resp.length);
        httpExchange.getResponseBody().write(resp);
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(CodeResponse.NOT_FOUND.getCode(), 0);
    }


    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(CodeResponse.OVERLAP.getCode(), 0);
    }

    protected void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(CodeResponse.NOT_ALLOWED.getCode(), 0);
    }

    protected int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}