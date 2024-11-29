package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Type type;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
        this.duration = Duration.ZERO;
        startTime = LocalDateTime.of(1, 1, 1, 0, 0);
    }

    public Task(String name, String description, LocalDateTime startTime, int durationInMinutes) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
        this.duration = Duration.ofMinutes(durationInMinutes);
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, type, name, status, description,
                startTime.format(DateTimeFormatter.ofPattern("HH:mm_dd.MM.yyyy")), duration.toMinutes());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }
}