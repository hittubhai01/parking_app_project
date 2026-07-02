package com.example.visionpark.network;

import com.google.gson.annotations.SerializedName;

public class CreateBookingRequest {
    @SerializedName("parkinglot_id")
    private int parkingLotId;

    @SerializedName("vehicle_id")
    private int vehicleId;

    @SerializedName("scheduled_start")
    private String scheduledStart;

    @SerializedName("scheduled_end")
    private String scheduledEnd;

    @SerializedName("payment_method")
    private String paymentMethod;

    public CreateBookingRequest(int parkingLotId, int vehicleId, String scheduledStart, String scheduledEnd, String paymentMethod) {
        this.parkingLotId = parkingLotId;
        this.vehicleId = vehicleId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.paymentMethod = paymentMethod;
    }

    public int getParkingLotId() { return parkingLotId; }
    public int getVehicleId() { return vehicleId; }
    public String getScheduledStart() { return scheduledStart; }
    public String getScheduledEnd() { return scheduledEnd; }
    public String getPaymentMethod() { return paymentMethod; }
}
