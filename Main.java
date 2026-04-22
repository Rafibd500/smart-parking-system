import java.util.*;


abstract class Vehicle {
    private String vehicleNumber;

    public Vehicle(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public abstract double calculateFee(long seconds);
}

class Car extends Vehicle {
    public Car(String vehicleNumber) {
        super(vehicleNumber);
    }

    @Override
    public double calculateFee(long seconds) {
        return seconds * 2.0;
    }
}

class Bike extends Vehicle {
    public Bike(String vehicleNumber) {
        super(vehicleNumber);
    }

    @Override
    public double calculateFee(long seconds) {
        return seconds * 1.0;
    }
}


class ParkingSlot {
    private final int slotId;
    private Vehicle parkedVehicle;
    private long entryTime;

    public ParkingSlot(int slotId) {
        this.slotId = slotId;
        this.parkedVehicle = null;
    }

    public int getSlotId() { return slotId; }
    
    public Vehicle getParkedVehicle() { 
        return parkedVehicle; 
    }
    
    public boolean isEmpty() { 
        return parkedVehicle == null; 
    }

    public long getEntryTime() { 
        return entryTime; 
    }

    public void park(Vehicle v) {
        this.parkedVehicle = v;
        this.entryTime = System.currentTimeMillis();
    }

    public void release() {
        this.parkedVehicle = null;
    }
}

abstract class ParkingSystem {
    protected ParkingSlot[] slots;

    public ParkingSystem(int capacity) {
        slots = new ParkingSlot[capacity];
        for (int i = 0; i < capacity; i++) {
            slots[i] = new ParkingSlot(i + 1);
        }
    }

    abstract void parkVehicle();
    abstract void removeVehicle();
    abstract void showStatus();
}

class SmartParking extends ParkingSystem {
    private Scanner sc = new Scanner(System.in);

    public SmartParking(int capacity) {
        super(capacity);
    }

    @Override
    void parkVehicle() {
        ParkingSlot availableSlot = null;
        for (ParkingSlot slot : slots) {
            if (slot.isEmpty()) {
                availableSlot = slot;
                break;
            }
        }

        if (availableSlot == null) {
            System.out.println("\nERROR: Parking Full!");
            return;
        }

        System.out.print("Enter Vehicle Type (Car/Bike): ");
        String type = sc.nextLine().trim().toLowerCase();
        System.out.print("Enter Vehicle Number: ");
        String vNum = sc.nextLine().trim();

        Vehicle v;
        if (type.equals("car")) {
            v = new Car(vNum);
        } 
        else if (type.equals("bike")) {
            v = new Bike(vNum);
        } 
        else {
            System.out.println("Invalid vehicle type. Parking aborted.");
            return;
        }

        availableSlot.park(v);
        System.out.println("Vehicle parked successfully at Slot " + availableSlot.getSlotId());
    }

    @Override
    void removeVehicle() {
        System.out.print("Enter Slot ID to clear: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (id < 1 || id > slots.length) {
            System.out.println("Invalid Slot ID.");
            return;
        }

        ParkingSlot slot = slots[id - 1];
        if (slot.isEmpty()) {
            System.out.println("Slot " + id + " is already empty.");
        } 
        else {
            long durationMillis = System.currentTimeMillis() - slot.getEntryTime();
            long seconds = durationMillis / 1000;
            double fee = slot.getParkedVehicle().calculateFee(seconds);

            System.out.println("\n===========================");
            System.out.println("      PARKING RECEIPT      ");
            System.out.println("===========================");
            System.out.println("Vehicle No : " + slot.getParkedVehicle().getVehicleNumber());
            System.out.println("Duration   : " + seconds + " seconds");
            System.out.println("Total Fee  : $" + String.format("%.2f", fee));
            System.out.println("===========================");

            slot.release();
            System.out.println("Slot " + id + " is now available.");
        }
    }

    @Override
    void showStatus() {
        System.out.println("\n--- Current Parking Status ---");
        System.out.printf("%-10s | %-20s\n", "SLOT ID", "STATUS/VEHICLE NO");
        System.out.println("----------------------------------");
        for (ParkingSlot slot : slots) {
            String status = slot.isEmpty() ? "Empty" : slot.getParkedVehicle().getVehicleNumber();
            System.out.printf("%-10d | %-20s\n", slot.getSlotId(), status);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner mainScanner = new Scanner(System.in);
        SmartParking parkingManager = new SmartParking(5);

        boolean exit = false;
        while (!exit) {
            System.out.println("\n----------------------------");
            System.out.println("   SMART PARKING SYSTEM    ");
            System.out.println("----------------------------");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Remove Vehicle");
            System.out.println("3. Show Parking Status");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            String input = mainScanner.nextLine();
            
            switch (input) {
                case "1":
                    parkingManager.parkVehicle();
                    break;
                case "2":
                    parkingManager.removeVehicle();
                    break;
                case "3":
                    parkingManager.showStatus();
                    break;
                case "4":
                    System.out.println("System shutting down. Have a great day!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid input, please try again.");
            }
        }
        mainScanner.close();
    }
}
