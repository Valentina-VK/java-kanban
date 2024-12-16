package server;

public enum CodeResponse {
    OK(200),
    MODIFIED(201),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    OVERLAP(406),
    SERVER_ERROR(500);

    private final int code;

    CodeResponse(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}