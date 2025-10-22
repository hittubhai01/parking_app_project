package com.example.visionpark.models;

public class ParkingLot {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String carFee;
    private String twoWheelerFee;
    private int availableCarSlots;
    private int totalCarSlots;
    private int availableTwoWheelerSlots;
    private int totalTwoWheelerSlots;
    private String paymentMode;
    private String address;
    private String landmark;
    private double distance; // For sorting by distance

    // Constructor
    public ParkingLot(int id, String name, double latitude, double longitude, 
                     String carFee, String twoWheelerFee, int availableCarSlots, 
                     int totalCarSlots, int availableTwoWheelerSlots, 
                     int totalTwoWheelerSlots, String paymentMode) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.carFee = carFee;
        this.twoWheelerFee = twoWheelerFee;
        this.availableCarSlots = availableCarSlots;
        this.totalCarSlots = totalCarSlots;
        this.availableTwoWheelerSlots = availableTwoWheelerSlots;
        this.totalTwoWheelerSlots = totalTwoWheelerSlots;
        this.paymentMode = paymentMode;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getCarFee() { return carFee; }
    public void setCarFee(String carFee) { this.carFee = carFee; }

    public String getTwoWheelerFee() { return twoWheelerFee; }
    public void setTwoWheelerFee(String twoWheelerFee) { this.twoWheelerFee = twoWheelerFee; }

    public int getAvailableCarSlots() { return availableCarSlots; }
    public void setAvailableCarSlots(int availableCarSlots) { this.availableCarSlots = availableCarSlots; }

    public int getTotalCarSlots() { return totalCarSlots; }
    public void setTotalCarSlots(int totalCarSlots) { this.totalCarSlots = totalCarSlots; }

    public int getAvailableTwoWheelerSlots() { return availableTwoWheelerSlots; }
    public void setAvailableTwoWheelerSlots(int availableTwoWheelerSlots) { this.availableTwoWheelerSlots = availableTwoWheelerSlots; }

    public int getTotalTwoWheelerSlots() { return totalTwoWheelerSlots; }
    public void setTotalTwoWheelerSlots(int totalTwoWheelerSlots) { this.totalTwoWheelerSlots = totalTwoWheelerSlots; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    // Helper methods
    public String getDisplayFee() {
        if (carFee != null && !carFee.isEmpty() && !carFee.equals("Free")) {
            return carFee;
        }
        return "Free";
    }

    public String getAvailabilityStatus() {
        int totalSlots = totalCarSlots + totalTwoWheelerSlots;
        int availableSlots = availableCarSlots + availableTwoWheelerSlots;
        
        if (availableSlots == 0) return "RED";
        if (availableSlots < totalSlots * 0.3) return "YELLOW";
        return "GREEN";
    }

    // For testParkingDataLoading method (Remove once data loading works correctly)
    public Object getCity() {
        return null;
    }
}