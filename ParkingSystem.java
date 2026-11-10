import java.util.ArrayList;
import java.util.Scanner;

/**
 * ParkingSystem - main class: menu loop + all parking operations.
 *
 * Data structures : ArrayList<Slot> (slots), WaitingQueue (FIFO, linked list)
 * Algorithms      : Linear search (free slot, vehicle lookup, slot lookup)
 */
public class ParkingSystem {

    private final ArrayList<Slot> slots = new ArrayList<>();
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final Scanner sc = new Scanner(System.in);

    public ParkingSystem(int capacity) {
        for (int i = 1; i <= capacity; i++) {
            slots.add(new Slot(i));
        }
    }

    // ------------------------------------------------------------------
    // Linear search methods  (O(n) each)
    // ------------------------------------------------------------------

    /** Returns the first free slot, or null if the lot is full. */
    private Slot findFreeSlot() {
        for (int i = 0; i < slots.size(); i++) {
            if (!slots.get(i).isOccupied()) return slots.get(i);
        }
        return null;
    }

    /** Returns the slot holding this vehicle, or null if not parked. */
    private Slot findSlotByVehicle(String vehicleId) {
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            if (s.isOccupied() && s.getVehicleId().equals(vehicleId)) return s;
        }
        return null;
    }

    /** Returns the slot with this ID, or null if the ID does not exist. */
    private Slot findSlotById(int slotId) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getId() == slotId) return slots.get(i);
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    /** Vehicle IDs: 3-12 characters, letters / digits / hyphen only. Stored in upper case. */
    private String readVehicleId(String prompt) {
        System.out.print(prompt);
        String id = sc.nextLine().trim().toUpperCase();
        if (!id.matches("[A-Z0-9-]{3,12}")) {
            System.out.println("Invalid vehicle ID. Use 3-12 letters/digits/hyphens (e.g. ABC-123).");
            return null;
        }
        return id;
    }

    private boolean alreadyInSystem(String vehicleId) {
        return findSlotByVehicle(vehicleId) != null || waitingQueue.contains(vehicleId);
    }

    // ------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------

    private void vehicleEntry() {
        String id = readVehicleId("Enter vehicle ID: ");
        if (id == null) return;

        if (alreadyInSystem(id)) {
            System.out.println("Duplicate entry! Vehicle " + id + " is already parked or waiting.");
            return;
        }

        Slot free = findFreeSlot();
        if (free != null) {
            free.park(id);
            System.out.println("Vehicle " + id + " allocated to Slot " + free.getId() + ".");
        } else {
            waitingQueue.enqueue(id);
            System.out.println("Parking is FULL. Vehicle " + id + " added to waiting queue (position "
                    + waitingQueue.positionOf(id) + ").");
        }
    }

    private void vehicleExit() {
        String id = readVehicleId("Enter vehicle ID to exit: ");
        if (id == null) return;

        Slot s = findSlotByVehicle(id);
        if (s == null) {
            if (waitingQueue.contains(id)) {
                System.out.println("Vehicle " + id + " is still in the waiting queue, not parked yet.");
            } else {
                System.out.println("Vehicle " + id + " not found in the parking lot.");
            }
            return;
        }

        s.free();
        System.out.println("Vehicle " + id + " has exited. Slot " + s.getId() + " is now free.");

        // Give the freed slot to the first vehicle waiting (FIFO)
        if (!waitingQueue.isEmpty()) {
            String next = waitingQueue.dequeue();
            s.park(next);
            System.out.println("Waiting vehicle " + next + " has been allocated Slot " + s.getId() + ".");
        }
    }

    private void displayAllSlots() {
        System.out.println("---- All Slots ----");
        for (Slot s : slots) System.out.println(s);
        System.out.println("Occupied: " + countOccupied() + " / " + slots.size());
    }

    private void displayFreeSlots() {
        System.out.println("---- Free Slots ----");
        boolean any = false;
        for (Slot s : slots) {
            if (!s.isOccupied()) {
                System.out.println("Slot " + s.getId());
                any = true;
            }
        }
        if (!any) System.out.println("No free slots. Parking is full.");
    }

    private void searchVehicle() {
        String id = readVehicleId("Enter vehicle ID to search: ");
        if (id == null) return;

        Slot s = findSlotByVehicle(id);
        if (s != null) {
            System.out.println("Vehicle " + id + " is parked in Slot " + s.getId() + ".");
        } else if (waitingQueue.contains(id)) {
            System.out.println("Vehicle " + id + " is in the waiting queue at position "
                    + waitingQueue.positionOf(id) + ".");
        } else {
            System.out.println("Vehicle " + id + " not found.");
        }
    }

    private void searchSlot() {
        System.out.print("Enter slot ID to search: ");
        String line = sc.nextLine().trim();
        int slotId;
        try {
            slotId = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        Slot s = findSlotById(slotId);
        if (s == null) {
            System.out.println("Slot " + slotId + " does not exist (valid: 1-" + slots.size() + ").");
        } else {
            System.out.println(s);
        }
    }

    private int countOccupied() {
        int c = 0;
        for (Slot s : slots) if (s.isOccupied()) c++;
        return c;
    }

    // ------------------------------------------------------------------
    // Menu loop
    // ------------------------------------------------------------------

    private void printMenu() {
        System.out.println("\n===== SMART PARKING SLOT ALLOCATION SYSTEM =====");
        System.out.println("Free slots: " + (slots.size() - countOccupied()) + " / " + slots.size()
                + "   | Waiting: " + waitingQueue.size());
        System.out.println("1. Vehicle Entry");
        System.out.println("2. Vehicle Exit");
        System.out.println("3. Display All Slots");
        System.out.println("4. Display Free Slots");
        System.out.println("5. Search by Vehicle ID");
        System.out.println("6. Search by Slot ID");
        System.out.println("7. Show Waiting Queue");
        System.out.println("8. Exit Application");
        System.out.print("Enter choice: ");
    }

    public void run() {
        while (true) {
            printMenu();
            if (!sc.hasNextLine()) break;
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1": vehicleEntry();        break;
                case "2": vehicleExit();         break;
                case "3": displayAllSlots();     break;
                case "4": displayFreeSlots();    break;
                case "5": searchVehicle();       break;
                case "6": searchSlot();          break;
                case "7": waitingQueue.display();break;
                case "8": System.out.println("Thank you. Goodbye!"); return;
                default:  System.out.println("Invalid choice. Please enter 1-8.");
            }
        }
    }

    public static void main(String[] args) {
        new ParkingSystem(5).run();   // 5 slots keeps the demo (full lot + queue) quick
    }
}
