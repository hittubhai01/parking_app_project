package com.example.visionpark.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.visionpark.R;
import com.example.visionpark.adapters.BookingAdapter;
import com.example.visionpark.models.ParkingBooking;
import com.example.visionpark.models.ParkingSession;
import com.example.visionpark.network.ApiClient;
import com.example.visionpark.network.ApiService;
import com.example.visionpark.network.BookingCheckInResponse;
import com.example.visionpark.utils.BookingDialogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingsActivity extends AppCompatActivity implements BookingAdapter.OnBookingActionListener {

    private static final String TAG = "BookingsActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private TabLayout tabLayout;
    private LinearLayout layoutEmptyState;
    private LinearLayout layoutLoading;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private FloatingActionButton fabAddBooking;

    private List<ParkingBooking> allBookings = new ArrayList<>();
    private ApiService apiService;
    private boolean isUpcomingTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        // Set up basic toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Bookings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupRecyclerView();
        setupTabs();
        setupClickListeners();
        setupBottomNavigation();

        // Load initial data
        loadBookings();
    }

    private void initializeViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        tabLayout = findViewById(R.id.tabLayout);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutLoading = findViewById(R.id.layoutLoading);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle);
        fabAddBooking = findViewById(R.id.fabAddBooking);
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(this, new ArrayList<>());
        bookingAdapter.setListener(this);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookings.setAdapter(bookingAdapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isUpcomingTab = tab.getPosition() == 0;
                loadBookings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadBookings);

        fabAddBooking.setOnClickListener(v -> {
            BookingDialogHelper.showCreateBookingDialog(this, null, this::loadBookings);
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_bookings);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
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
                finish();
                return true;
            } else if (itemId == R.id.nav_bookings) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(BookingsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadBookings() {
        showLoading(true);

        Call<ApiService.ApiResponse<List<ParkingBooking>>> call;
        if (isUpcomingTab) {
            call = apiService.getUpcomingBookings();
        } else {
            call = apiService.getBookings(null);
        }

        call.enqueue(new Callback<ApiService.ApiResponse<List<ParkingBooking>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<List<ParkingBooking>>> call, 
                                   @NonNull Response<ApiService.ApiResponse<List<ParkingBooking>>> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allBookings = response.body().getData();
                    if (allBookings == null) {
                        allBookings = new ArrayList<>();
                    }
                    bookingAdapter.updateBookings(allBookings);
                    updateUIState();
                } else {
                    Log.e(TAG, "Server error loading bookings: " + response.code());
                    Toast.makeText(BookingsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    updateUIState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<List<ParkingBooking>>> call, 
                                  @NonNull Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Network error loading bookings", t);
                Toast.makeText(BookingsActivity.this, "Network error loading bookings", Toast.LENGTH_SHORT).show();
                updateUIState();
            }
        });
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerViewBookings.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateUIState() {
        if (allBookings.isEmpty()) {
            recyclerViewBookings.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            
            if (isUpcomingTab) {
                tvEmptyTitle.setText("No Upcoming Bookings");
                tvEmptySubtitle.setText("Reserve a future slot using the + button below");
            } else {
                tvEmptyTitle.setText("No Bookings Found");
                tvEmptySubtitle.setText("Your booking history will be shown here");
            }
        } else {
            recyclerViewBookings.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancelBooking(ParkingBooking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking");
        builder.setMessage("Are you sure you want to cancel booking #" + booking.getBookingId() + "?");

        final EditText input = new EditText(this);
        input.setHint("Reason for cancellation (optional)");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Yes, Cancel", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (reason.isEmpty()) reason = "User requested cancellation";
            
            showLoading(true);
            apiService.cancelBooking(booking.getBookingId(), reason).enqueue(new Callback<ApiService.ApiResponse<Void>>() {
                @Override
                public void onResponse(@NonNull Call<ApiService.ApiResponse<Void>> call, 
                                       @NonNull Response<ApiService.ApiResponse<Void>> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(BookingsActivity.this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        Toast.makeText(BookingsActivity.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiService.ApiResponse<Void>> call, @NonNull Throwable t) {
                    showLoading(false);
                    Toast.makeText(BookingsActivity.this, "Network error cancelling booking", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onCheckinBooking(ParkingBooking booking) {
        showLoading(true);
        apiService.checkinBooking(booking.getBookingId()).enqueue(new Callback<ApiService.ApiResponse<BookingCheckInResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<BookingCheckInResponse>> call, 
                                   @NonNull Response<ApiService.ApiResponse<BookingCheckInResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BookingCheckInResponse data = response.body().getData();
                    String ticketId = data != null ? data.getTicketId() : "";
                    
                    Toast.makeText(BookingsActivity.this, "Checked in successfully! Session ticket: " + ticketId, Toast.LENGTH_LONG).show();

                    // Navigate to MySessionsActivity
                    Intent intent = new Intent(BookingsActivity.this, MySessionsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BookingsActivity.this, "Failed to check in", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<BookingCheckInResponse>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(BookingsActivity.this, "Network error checking in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}