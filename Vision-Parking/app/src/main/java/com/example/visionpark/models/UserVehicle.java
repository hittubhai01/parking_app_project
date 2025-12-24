package com.example.visionpark.models;

import java.io.Serializable;

/**
 * Model class representing a user's vehicle
 * Matches the backend UserVehicle model structure
 */
public class UserVehicle implements Serializable {
    private int vehicleId;
    private int userId;
    private String registrationNumber;
    private String vehicleName;
    private String make;
    private String model;
    private Integer year;
    private String vehicleType;
    private String color;
    private boolean isActive;
    private String createdAt;
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
                '}';
    }
}