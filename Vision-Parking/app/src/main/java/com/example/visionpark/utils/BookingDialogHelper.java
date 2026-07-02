package com.example.visionpark.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.visionpark.R;
import com.example.visionpark.models.ParkingBooking;
import com.example.visionpark.models.ParkingLot;
import com.example.visionpark.models.UserVehicle;
import com.example.visionpark.network.ApiClient;
import com.example.visionpark.network.ApiService;
import com.example.visionpark.network.BookingAvailability;
import com.example.visionpark.network.CreateBookingRequest;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDialogHelper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static void showCreateBookingDialog(@NonNull Context context, 
                                               @Nullable ParkingLot preSelectedLot, 
                                               @NonNull Runnable onBookingCreatedListener) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_booking, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();

        // Bind Views
        Spinner spinnerParkingLot = dialogView.findViewById(R.id.spinnerParkingLot);
        Spinner spinnerVehicle = dialogView.findViewById(R.id.spinnerVehicle);
        MaterialButton btnPickDate = dialogView.findViewById(R.id.btnPickDate);
        MaterialButton btnPickStartTime = dialogView.findViewById(R.id.btnPickStartTime);
        MaterialButton btnPickEndTime = dialogView.findViewById(R.id.btnPickEndTime);
        
        CardView cardAvailability = dialogView.findViewById(R.id.cardAvailability);
        TextView tvAvailableSlots = dialogView.findViewById(R.id.tvAvailableSlots);
        TextView tvEstimatedCost = dialogView.findViewById(R.id.tvEstimatedCost);
        
        MaterialButton btnCheckAvailability = dialogView.findViewById(R.id.btnCheckAvailability);
        MaterialButton btnCheckAvailabilityMain = dialogView.findViewById(R.id.btnCheckAvailabilityMain);
        MaterialButton btnConfirmBooking = dialogView.findViewById(R.id.btnConfirmBooking);

        // Date and Time State Variables
        final Calendar calendar = Calendar.getInstance();
        final Calendar startCalendar = Calendar.getInstance();
        final Calendar endCalendar = Calendar.getInstance();
        
        // Add 1 hour to end calendar by default
        endCalendar.add(Calendar.HOUR_OF_DAY, 1);

        // Initial view values
        btnPickDate.setText(DATE_FORMAT.format(calendar.getTime()));
        btnPickStartTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(startCalendar.getTime()));
        btnPickEndTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(endCalendar.getTime()));

        final String[] dateStr = { DATE_FORMAT.format(calendar.getTime()) };
        final String[] startTimeStr = { TIME_FORMAT.format(startCalendar.getTime()) };
        final String[] endTimeStr = { TIME_FORMAT.format(endCalendar.getTime()) };

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Populate Vehicles
        List<UserVehicle> vehiclesList = new ArrayList<>();
        ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new ArrayList<>());
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicle.setAdapter(vehicleAdapter);

        apiService.getUserVehicles().enqueue(new Callback<ApiService.ApiResponse<List<UserVehicle>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<List<UserVehicle>>> call, Response<ApiService.ApiResponse<List<UserVehicle>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    vehiclesList.clear();
                    vehiclesList.addAll(response.body().getData());
                    
                    List<String> vehicleNames = new ArrayList<>();
                    for (UserVehicle v : vehiclesList) {
                        vehicleNames.add(v.getDisplayName());
                    }
                    vehicleAdapter.clear();
                    vehicleAdapter.addAll(vehicleNames);
                    vehicleAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to load vehicles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<List<UserVehicle>>> call, Throwable t) {
                Toast.makeText(context, "Network error loading vehicles", Toast.LENGTH_SHORT).show();
            }
        });

        // Populate Parking Lots
        List<ParkingLot> lotsList = new ArrayList<>();
        ArrayAdapter<String> lotsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new ArrayList<>());
        lotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParkingLot.setAdapter(lotsAdapter);

        apiService.getNearbyParkingLots(28.6315, 77.2167, 50000, null, null, "car").enqueue(new Callback<ApiService.ApiResponse<List<ParkingLot>>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<List<ParkingLot>>> call, Response<ApiService.ApiResponse<List<ParkingLot>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    lotsList.clear();
                    lotsList.addAll(response.body().getData());

                    List<String> lotNames = new ArrayList<>();
                    int selectionIndex = 0;
                    for (int i = 0; i < lotsList.size(); i++) {
                        ParkingLot lot = lotsList.get(i);
                        lotNames.add(lot.getName());
                        if (preSelectedLot != null && lot.getId() == preSelectedLot.getId()) {
                            selectionIndex = i;
                        }
                    }
                    lotsAdapter.clear();
                    lotsAdapter.addAll(lotNames);
                    lotsAdapter.notifyDataSetChanged();

                    if (!lotsList.isEmpty()) {
                        spinnerParkingLot.setSelection(selectionIndex);
                    }
                } else {
                    Toast.makeText(context, "Failed to load parking lots", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<List<ParkingLot>>> call, Throwable t) {
                Toast.makeText(context, "Network error loading parking lots", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Date Picker
        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        
                        dateStr[0] = DATE_FORMAT.format(calendar.getTime());
                        btnPickDate.setText(dateStr[0]);
                        
                        // Invalidate availability since date changed
                        btnConfirmBooking.setEnabled(false);
                        btnConfirmBooking.setAlpha(0.6f);
                        cardAvailability.setVisibility(View.GONE);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // Don't allow bookings in the past
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // Setup Start Time Picker
        btnPickStartTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> {
                        startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCalendar.set(Calendar.MINUTE, minute);
                        
                        startTimeStr[0] = TIME_FORMAT.format(startCalendar.getTime());
                        btnPickStartTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(startCalendar.getTime()));
                        
                        // Invalidate availability
                        btnConfirmBooking.setEnabled(false);
                        btnConfirmBooking.setAlpha(0.6f);
                        cardAvailability.setVisibility(View.GONE);
                    },
                    startCalendar.get(Calendar.HOUR_OF_DAY),
                    startCalendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });

        // Setup End Time Picker
        btnPickEndTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> {
                        endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCalendar.set(Calendar.MINUTE, minute);
                        
                        endTimeStr[0] = TIME_FORMAT.format(endCalendar.getTime());
                        btnPickEndTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(endCalendar.getTime()));
                        
                        // Invalidate availability
                        btnConfirmBooking.setEnabled(false);
                        btnConfirmBooking.setAlpha(0.6f);
                        cardAvailability.setVisibility(View.GONE);
                    },
                    endCalendar.get(Calendar.HOUR_OF_DAY),
                    endCalendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });

        // Helper check availability handler
        View.OnClickListener checkAvailabilityListener = v -> {
            if (lotsList.isEmpty() || spinnerParkingLot.getSelectedItemPosition() < 0) {
                Toast.makeText(context, "Please select a parking lot", Toast.LENGTH_SHORT).show();
                return;
            }
            if (vehiclesList.isEmpty() || spinnerVehicle.getSelectedItemPosition() < 0) {
                Toast.makeText(context, "Please select a vehicle", Toast.LENGTH_SHORT).show();
                return;
            }

            ParkingLot lot = lotsList.get(spinnerParkingLot.getSelectedItemPosition());
            UserVehicle vehicle = vehiclesList.get(spinnerVehicle.getSelectedItemPosition());
            String vehicleType = vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "car";

            apiService.checkBookingAvailability(lot.getId(), dateStr[0], startTimeStr[0], endTimeStr[0], vehicleType)
                    .enqueue(new Callback<ApiService.ApiResponse<BookingAvailability>>() {
                        @Override
                        public void onResponse(Call<ApiService.ApiResponse<BookingAvailability>> call, Response<ApiService.ApiResponse<BookingAvailability>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                BookingAvailability availability = response.body().getData();
                                if (availability != null && availability.isAvailable()) {
                                    cardAvailability.setVisibility(View.VISIBLE);
                                    btnCheckAvailabilityMain.setVisibility(View.GONE);
                                    
                                    tvAvailableSlots.setText(availability.getAvailableSlots() + " slots available");
                                    tvEstimatedCost.setText(String.format(Locale.getDefault(), "Estimated Cost: Rs.%.2f", availability.getEstimatedCost()));
                                    
                                    btnConfirmBooking.setEnabled(true);
                                    btnConfirmBooking.setAlpha(1.0f);
                                } else {
                                    cardAvailability.setVisibility(View.GONE);
                                    btnCheckAvailabilityMain.setVisibility(View.VISIBLE);
                                    btnConfirmBooking.setEnabled(false);
                                    btnConfirmBooking.setAlpha(0.6f);
                                    Toast.makeText(context, "No slots available for the selected time window", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context, "Error checking availability: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiService.ApiResponse<BookingAvailability>> call, Throwable t) {
                            Toast.makeText(context, "Network error checking availability", Toast.LENGTH_SHORT).show();
                        }
                    });
        };

        btnCheckAvailability.setOnClickListener(checkAvailabilityListener);
        btnCheckAvailabilityMain.setOnClickListener(checkAvailabilityListener);

        // Confirm Booking Click
        btnConfirmBooking.setOnClickListener(v -> {
            if (lotsList.isEmpty() || spinnerParkingLot.getSelectedItemPosition() < 0) return;
            if (vehiclesList.isEmpty() || spinnerVehicle.getSelectedItemPosition() < 0) return;

            ParkingLot lot = lotsList.get(spinnerParkingLot.getSelectedItemPosition());
            UserVehicle vehicle = vehiclesList.get(spinnerVehicle.getSelectedItemPosition());

            String scheduledStart = dateStr[0] + "T" + startTimeStr[0] + ":00";
            String scheduledEnd = dateStr[0] + "T" + endTimeStr[0] + ":00";

            CreateBookingRequest request = new CreateBookingRequest(
                    lot.getId(),
                    vehicle.getVehicleId(),
                    scheduledStart,
                    scheduledEnd,
                    "card"
            );

            apiService.createBooking(request).enqueue(new Callback<ApiService.ApiResponse<ParkingBooking>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponse<ParkingBooking>> call, Response<ApiService.ApiResponse<ParkingBooking>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(context, "Booking confirmed successfully!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        onBookingCreatedListener.run();
                    } else {
                        Toast.makeText(context, "Failed to create booking", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponse<ParkingBooking>> call, Throwable t) {
                    Toast.makeText(context, "Network error creating booking", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Reset state on spinner changes
        spinnerParkingLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btnConfirmBooking.setEnabled(false);
                btnConfirmBooking.setAlpha(0.6f);
                cardAvailability.setVisibility(View.GONE);
                btnCheckAvailabilityMain.setVisibility(View.VISIBLE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btnConfirmBooking.setEnabled(false);
                btnConfirmBooking.setAlpha(0.6f);
                cardAvailability.setVisibility(View.GONE);
                btnCheckAvailabilityMain.setVisibility(View.VISIBLE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialog.show();
    }
}
