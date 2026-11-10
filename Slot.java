/**
 * Slot - simple data holder for one parking slot.
 * Stores the slot ID, its status (free/occupied) and the vehicle parked in it.
 */
public class Slot {
    private final int id;
    private boolean occupied;
    private String vehicleId;   // null when the slot is free

    public Slot(int id) {
        this.id = id;
        this.occupied = false;
        this.vehicleId = null;
    }

    public int getId()            { return id; }
    public boolean isOccupied()   { return occupied; }
    public String getVehicleId()  { return vehicleId; }

    /** Marks the slot as full and stores the vehicle ID. */
    public void park(String vehicleId) {
        this.vehicleId = vehicleId;
        this.occupied = true;
    }

    /** Marks the slot as empty and clears the vehicle ID. */
    public void free() {
        this.vehicleId = null;
        this.occupied = false;
    }

    @Override
    public String toString() {
        return "Slot " + id + " : " + (occupied ? "OCCUPIED by " + vehicleId : "FREE");
    }
}
