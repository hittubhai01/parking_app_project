package com.example.visionpark.network;

import com.google.gson.annotations.SerializedName;

public class BookingCheckInResponse {
    @SerializedName("booking_id")
    private String bookingId;

    @SerializedName("ticket_id")
    private String ticketId;

    @SerializedName("parking_lot_name")
    private String parkingLotName;

    @SerializedName("slot_location")
    private String slotLocation;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("status")
    private String status;

    public String getBookingId() { return bookingId; }
    public String getTicketId() { return ticketId; }
    public String getParkingLotName() { return parkingLotName; }
    public String getSlotLocation() { return slotLocation; }
    public String getStartTime() { return startTime; }
    public String getStatus() { return status; }
}
