package com.example.visionpark.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.visionpark.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.content.Intent;
import androidx.annotation.NonNull;

public class BookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);
        
        // Set up basic toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Bookings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("visionpark_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "User");

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_bookings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(BookingsActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_sessions) {
                    Intent intent = new Intent(BookingsActivity.this, MySessionsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_bookings) {
                    // Already on Bookings
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(BookingsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Set placeholder text with user's name
        TextView tvPlaceholder = findViewById(R.id.tvPlaceholder);
        if (tvPlaceholder != null) {
            tvPlaceholder.setText("Welcome " + username + "!\n\nYour bookings will be displayed here.");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 