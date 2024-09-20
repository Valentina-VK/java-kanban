package task;
import history.STATUS;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected STATUS status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = STATUS.NEW;
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
        return String.format("id: %d, name: %s, description.length: %d, status: %s",
                id, name, description.length(), status);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public STATUS getStatus() {
        return status;
    }
}
