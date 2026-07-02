package com.example.visionpark.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model class representing a user's vehicle
 * Matches the backend UserVehicle model structure
 */
public class UserVehicle implements Serializable {
    @SerializedName("vehicle_id")
    private int vehicleId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("registration_number")
    private String registrationNumber;

    @SerializedName("vehicle_name")
    private String vehicleName;

    private String make;
    private String model;
    private Integer year;

    @SerializedName("vehicle_type")
    private String vehicleType;

    private String color;

    @SerializedName("is_active")
    private boolean isActive = true;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;
    
    // Default constructor
    public UserVehicle() {}
    
    // Constructor with required fields
    public UserVehicle(String registrationNumber, String vehicleName) {
        this.registrationNumber = registrationNumber;
        this.vehicleName = vehicleName;
        this.isActive = true;
    }
    
    // Full constructor
    public UserVehicle(int vehicleId, int userId, String registrationNumber, String vehicleName,
                      String make, String model, Integer year, String vehicleType, String color) {
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.vehicleName = vehicleName;
        this.make = make;
        this.model = model;
        this.year = year;
        this.vehicleType = vehicleType;
        this.color = color;
        this.isActive = true;
    }
    
    // Additional constructor for tests (4 parameters)
    public UserVehicle(int vehicleId, String registrationNumber, String vehicleName, String vehicleType) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.isActive = true;
    }

    // Additional constructor for tests (8 parameters)
    public UserVehicle(int vehicleId, String registrationNumber, String vehicleName,
                      String make, String model, Integer year, String vehicleType, String color) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.vehicleName = vehicleName;
        this.make = make;
        this.model = model;
        this.year = year;
        this.vehicleType = vehicleType;
        this.color = color;
        this.isActive = true;
    }

    // Helper method to get formatted display name
    public String getDisplayName() {
        if (vehicleName != null && !vehicleName.trim().isEmpty()) {
            return vehicleName + " (" + registrationNumber + ")";
        }
        return registrationNumber;
    }

    // Helper method to get detailed vehicle specifications
    public String getVehicleDetails() {
        StringBuilder sb = new StringBuilder();
        if (make != null && !make.trim().isEmpty()) {
            sb.append(make);
        }
        if (model != null && !model.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(model);
        }
        if (year != null && year > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(year);
        }
        return sb.length() > 0 ? sb.toString() : "Vehicle details not available";
    }

    // Helper method to get user-friendly vehicle type
    public String getFormattedVehicleType() {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            return "Car";
        }
        String type = vehicleType.toLowerCase().trim();
        if (type.equals("car")) {
            return "Car";
        } else if (type.equals("motorcycle")) {
            return "Motorcycle";
        } else if (type.equals("two_wheeler")) {
            return "Two_wheeler";
        }
        // Fallback: capitalize first letter, keep underscores
        if (type.length() > 0) {
            return type.substring(0, 1).toUpperCase() + type.substring(1);
        }
        return "Car";
    }

    
    // Getters and Setters
    public int getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public String getVehicleName() {
        return vehicleName;
    }
    
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "UserVehicle{" +
                "vehicleId=" + vehicleId +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", vehicleType='" + vehicleType + '\'' +
                ", color='" + color + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}