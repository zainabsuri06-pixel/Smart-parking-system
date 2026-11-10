/**
 * WaitingQueue - FIFO queue of vehicle IDs waiting for a slot.
 * Implemented from scratch with a linked list (front = dequeue, rear = enqueue),
 * so enqueue and dequeue are both O(1).
 */
public class WaitingQueue {

    private static class Node {
        String vehicleId;
        Node next;
        Node(String vehicleId) { this.vehicleId = vehicleId; }
    }

    private Node front;
    private Node rear;
    private int size;

    /** Adds a vehicle at the rear of the queue. */
    public void enqueue(String vehicleId) {
        Node node = new Node(vehicleId);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /** Removes and returns the vehicle at the front, or null if empty. */
    public String dequeue() {
        if (front == null) return null;
        String id = front.vehicleId;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return id;
    }

    public String peek()       { return front == null ? null : front.vehicleId; }
    public boolean isEmpty()   { return size == 0; }
    public int size()          { return size; }

    /** Linear search - used to stop the same vehicle from queueing twice. */
    public boolean contains(String vehicleId) {
        for (Node cur = front; cur != null; cur = cur.next) {
            if (cur.vehicleId.equals(vehicleId)) return true;
        }
        return false;
    }

    /** Returns 1-based position in the queue, or -1 if not present. */
    public int positionOf(String vehicleId) {
        int pos = 1;
        for (Node cur = front; cur != null; cur = cur.next, pos++) {
            if (cur.vehicleId.equals(vehicleId)) return pos;
        }
        return -1;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Waiting queue is empty.");
            return;
        }
        System.out.println("Waiting queue (front -> rear):");
        int pos = 1;
        for (Node cur = front; cur != null; cur = cur.next) {
            System.out.println("  " + pos++ + ". " + cur.vehicleId);
        }
    }
}
