package history;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;
    private Node last;
    Map<Integer, Node> mapOfNodes = new HashMap<>();

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }


    @Override
    public void addHistory(Task task) {
        if (task != null) {
            if (mapOfNodes.containsKey(task.getId())) {
                removeNode(mapOfNodes.get(task.getId()));
            }
            linkLast(task);
            mapOfNodes.put(task.getId(), last);
        }
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node f = first;
        while (f != null) {
            history.add(f.item);
            f = f.next;
        }
        return history;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (first == last && first == node) {
            first = null;
            last = null;
            return;
        }
        if (first == node) {
            nextNode.prev = null;
            first = nextNode;
        } else if (last == node) {
            prevNode.next = null;
            last = prevNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (mapOfNodes.containsKey(id)) {
            removeNode(mapOfNodes.get(id));
            mapOfNodes.remove(id);
        }
    }
}