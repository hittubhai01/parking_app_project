package com.example.visionpark.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.visionpark.R;
import com.example.visionpark.models.ParkingLot;
import com.google.maps.android.ui.IconGenerator;

public class MarkerGenerator {
    private Context context;
    private IconGenerator iconGenerator;
    
    public MarkerGenerator(Context context) {
        this.context = context;
        iconGenerator = new IconGenerator(context);
    }
    
    public Bitmap createParkingMarker(ParkingLot parkingLot) {
        // Create custom layout for marker
        View markerView = LayoutInflater.from(context)
            .inflate(R.layout.custom_marker_layout, null);
        
        TextView priceText = markerView.findViewById(R.id.price_text);
        ImageView pinIcon = markerView.findViewById(R.id.pin_icon);
        
        // Set price text
        priceText.setText(parkingLot.getDisplayFee());
        
        // Set pin color based on availability
        String status = parkingLot.getAvailabilityStatus();
        int pinColor = getPinColor(status);
        pinIcon.setColorFilter(pinColor);
        
        // Configure icon generator
        iconGenerator.setContentView(markerView);
        iconGenerator.setBackground(null);
        
        return iconGenerator.makeIcon();
    }
    
    private int getPinColor(String status) {
        switch (status) {
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "RED": return Color.RED;
            default: return Color.GRAY;
        }
    }
}
