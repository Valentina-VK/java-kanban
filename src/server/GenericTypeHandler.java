package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class GenericTypeHandler extends BaseHttpHandler implements HttpHandler {

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

    protected abstract void getByRequest(HttpExchange httpExchange) throws IOException;
}
