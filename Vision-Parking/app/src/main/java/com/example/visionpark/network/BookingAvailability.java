package com.example.visionpark.network;

import com.google.gson.annotations.SerializedName;

public class BookingAvailability {
    @SerializedName("parkinglot_id")
    private int parkingLotId;

    @SerializedName("parking_lot_name")
    private String parkingLotName;

    @SerializedName("available_slots")
    private int availableSlots;

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("hourly_rate")
    private double hourlyRate;

    @SerializedName("estimated_cost")
    private double estimatedCost;

    @SerializedName("duration_hours")
    private double durationHours;

    public int getParkingLotId() { return parkingLotId; }
    public String getParkingLotName() { return parkingLotName; }
    public int getAvailableSlots() { return availableSlots; }
    public boolean isAvailable() { return isAvailable; }
    public double getHourlyRate() { return hourlyRate; }
    public double getEstimatedCost() { return estimatedCost; }
    public double getDurationHours() { return durationHours; }
}
