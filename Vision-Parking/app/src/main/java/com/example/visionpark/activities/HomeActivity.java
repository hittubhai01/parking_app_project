package com.example.visionpark.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.common.api.Status;

import com.example.visionpark.R;
import com.example.visionpark.utils.TokenManager;
import com.example.visionpark.utils.ParkingDataLoader;
import com.example.visionpark.utils.MarkerGenerator;
import com.example.visionpark.models.ParkingLot;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "HomeActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar topAppBar;
    private ActionBarDrawerToggle drawerToggle;
    private GoogleMap mMap;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabLocation;
    
    // Location related variables
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean locationPermissionGranted = false;
    
    // Places API related variables
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;
    
    // Parking lot related variables
    private MarkerGenerator markerGenerator;
    private List<Marker> parkingMarkers = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        checkLocationPermission();

        // Initialize Places API
        initializePlaces();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        topAppBar = findViewById(R.id.topAppBar);
        fabLocation = findViewById(R.id.fabLocation);

        // Set up toolbar with hamburger icon
        setSupportActionBar(topAppBar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Set username in drawer header
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);
        android.content.SharedPreferences prefs = getSharedPreferences("visionpark_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        tvUserName.setText(username);

        // Set up header click listener for navigation to Profile
        LinearLayout headerContainer = headerView.findViewById(R.id.headerContainer);
        headerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the drawer first
                drawerLayout.closeDrawers();
                
                // Navigate to ProfileActivity
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Navigation drawer item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {
                    TokenManager.clearToken(HomeActivity.this);
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.nav_sessions) {
                    Intent intent = new Intent(HomeActivity.this, MySessionsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_bookings) {
                    Intent intent = new Intent(HomeActivity.this, BookingsActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    // Handle other drawer items (show Toast for now)
                    Toast.makeText(HomeActivity.this, item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Already on home screen
                    Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_sessions) {
                    Intent intent = new Intent(HomeActivity.this, MySessionsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_bookings) {
                    Intent intent = new Intent(HomeActivity.this, BookingsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Set up Places Autocomplete
        setupPlacesAutocomplete();
        
        // Set up location button functionality
        setupLocationButton();

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Initialize Places API
    private void initializePlaces() {
        if (!Places.isInitialized()) {
            try {
                android.content.pm.ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), android.content.pm.PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String apiKey = bundle.getString("com.google.android.geo.API_KEY");
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), apiKey);
                }
            } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
            }

        }
        placesClient = Places.createClient(this);
    }

    // Update the setupPlacesAutocomplete method to call the styling method
    // Setup Places Autocomplete
    private void setupPlacesAutocomplete() {
        // Get the autocomplete fragment
        autocompleteFragment = (AutocompleteSupportFragment)
            getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        
        // Set the place fields to be returned
        autocompleteFragment.setPlaceFields(Arrays.asList(
            Place.Field.ID, 
            Place.Field.NAME, 
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        ));
        
        // Set up the PlaceSelectionListener
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    onSearchLocationSelected(latLng);
                }
            }
            
            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "Error: " + status);
                Toast.makeText(HomeActivity.this, "Search failed: " + status.getStatusMessage(), 
                              Toast.LENGTH_SHORT).show();
            }
        });
        
        // Apply custom styling
        styleAutocompleteFragment();
    }
    // Style the autocomplete fragment to match the design
    private void styleAutocompleteFragment() {
        if (autocompleteFragment != null) {
            // Set hint text
            autocompleteFragment.setHint("Where do you want to park?");
            
            // You can also customize other properties if needed
            // Note: Some styling might need to be done through themes/styles
        }
    }

    // Handle place selection and move map camera
    private void onSearchLocationSelected(LatLng location) {
        try {
            // Show loading state
            showLoadingState();
            
            // Move camera to selected location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            
            // Clear existing markers
            clearParkingMarkers();
            
            // Fetch nearby parking lots
            fetchNearbyParking(location.latitude, location.longitude);
            
            // Hide loading state
            hideLoadingState();
            
        } catch (Exception e) {
            hideLoadingState();
            handleSearchError("Failed to search location: " + e.getMessage());
        }
    }

    // Fetch nearby parking lots
    private void fetchNearbyParking(double lat, double lng) {
        showLoadingState();
        
        try {
            // Try API first (when backend is available)
            fetchParkingFromAPI(lat, lng);
            
            // Fallback to local data from SQL
            List<ParkingLot> allLots = ParkingDataLoader.loadParkingLotsFromAssets(this);
            List<ParkingLot> nearbyLots = ParkingDataLoader.getNearbyParkingLots(
               this, allLots, lat, lng, 5.0); // 5km radius
            
            // Apply performance optimization
            optimizeMapPerformance(nearbyLots);
            
            displayParkingLotsOnMap(nearbyLots);
            hideLoadingState();
            
        } catch (Exception e) {
            hideLoadingState();
            handleSearchError("Failed to load parking data: " + e.getMessage());
        }
    }

    // Future API integration
    private void fetchParkingFromAPI(double lat, double lng) {
        // TODO: Implement API call to GET /parking?lat=&lng=&radius=
        // This will be implemented when backend is available
        Log.d(TAG, "API integration not yet implemented, using local data");
    }

    // Display parking lots on map
    private void displayParkingLotsOnMap(List<ParkingLot> parkingLots) {
        try {
            // Clear existing markers
            clearParkingMarkers();
            
            if (parkingLots == null || parkingLots.isEmpty()) {
                Toast.makeText(this, "No parking lots found in this area", Toast.LENGTH_SHORT).show();
                return;
            }
            
            markerGenerator = new MarkerGenerator(this);
            
            for (ParkingLot lot : parkingLots) {
                LatLng position = new LatLng(lot.getLatitude(), lot.getLongitude());
                
                Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(markerGenerator.createParkingMarker(lot)))
                    .title(lot.getName())
                    .snippet(lot.getDisplayFee()));
                
                // Store reference for later use
                marker.setTag(lot);
                parkingMarkers.add(marker);
            }
            
            Log.d(TAG, "Displayed " + parkingLots.size() + " parking lots on map");
            
        } catch (Exception e) {
            handleSearchError("Failed to display parking lots: " + e.getMessage());
        }
    }

    // Clear parking markers
    private void clearParkingMarkers() {
        for (Marker marker : parkingMarkers) {
            marker.remove();
        }
        parkingMarkers.clear();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Move camera to a default location (e.g., New Delhi)
        LatLng defaultLocation = new LatLng(28.6139, 77.2090);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
        // Update map with location settings if permission is granted
        updateMapWithLocationSettings();
        
        // Add marker click handler
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ParkingLot parkingLot = (ParkingLot) marker.getTag();
                if (parkingLot != null) {
                    showParkingLotDetails(parkingLot);
                }
                return true;
            }
        });
    }

    // Show parking lot details
    private void showParkingLotDetails(ParkingLot parkingLot) {
        String details = "Name: " + parkingLot.getName() + "\n" +
                        "Fee: " + parkingLot.getDisplayFee() + "\n" +
                        "Available Car Slots: " + parkingLot.getAvailableCarSlots() + 
                        "/" + parkingLot.getTotalCarSlots() + "\n" +
                        "Available Two-Wheeler Slots: " + parkingLot.getAvailableTwoWheelerSlots() + 
                        "/" + parkingLot.getTotalTwoWheelerSlots();
        
        Toast.makeText(this, details, Toast.LENGTH_LONG).show();
    }
    
    private void updateMapWithLocationSettings() {
        if (mMap == null) {
            return;
        }
        
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                
                // Get the current location once the map is ready
                getCurrentLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Error setting location settings: " + e.getMessage());
        }
    }

    private void setupLocationButton() {
        // Set up "Use Current Location" floating action button
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Getting current location...", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
        });
    }
    
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateMapWithLocationSettings();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                updateMapWithLocationSettings();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Cannot show current location.", 
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateMapWithLocation(location);
                }
            }
        };
    }
    
    private void getCurrentLocation() {
        if (!locationPermissionGranted) {
            checkLocationPermission();
            return;
        }
        
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            
            // Improve location detection with high accuracy
            LocationRequest locationRequest = new LocationRequest.Builder(10000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(5000)
                .build();
                
            // Try to get the last known location
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d(TAG, "Got location: " + location.getLatitude() + ", " + location.getLongitude());
                        updateMapWithLocation(location);
                    } else {
                        Log.d(TAG, "Last location is null, requesting location updates");
                        // If last location is null, request location updates
                        requestLocationUpdates();
                        
                        // Show message that location is not available
                        Toast.makeText(HomeActivity.this, "Location not available. Please enable location services.", 
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting location: " + e.getMessage());
                    Toast.makeText(this, "Unable to get current location. Please check location settings.", 
                            Toast.LENGTH_LONG).show();
                    
                    // Request location updates as a fallback
                    requestLocationUpdates();
                });
        } catch (Exception e) {
            Log.e(TAG, "Error in getCurrentLocation: " + e.getMessage());
            Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void requestLocationUpdates() {
        try {
            if (!locationPermissionGranted) {
                return;
            }
            
            LocationRequest locationRequest = new LocationRequest.Builder(10000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMinUpdateIntervalMillis(5000)
                    .build();
            
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } catch (Exception e) {
            Log.e(TAG, "Error requesting location updates: " + e.getMessage());
        }
    }
    
    private void updateMapWithLocation(Location location) {
        if (mMap == null || location == null) {
            return;
        }
        
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear(); // Clear previous markers
        mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Your Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
        
        // Fetch nearby parking lots for current location
        fetchNearbyParking(location.getLatitude(), location.getLongitude());
        
        Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show();
    }

    // Add loading states and error handling
    private void showLoadingState() {
        // Show loading indicator
        Toast.makeText(this, "Searching for parking lots...", Toast.LENGTH_SHORT).show();
        // You can also show a ProgressBar or loading dialog here
    }

    private void hideLoadingState() {
        // Hide loading indicator
        // Hide ProgressBar or loading dialog if shown
    }

    private void handleSearchError(String error) {
        Toast.makeText(this, "Search failed: " + error, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Search error: " + error);
    }

    // Add performance optimizations
    private void optimizeMapPerformance(List<ParkingLot> parkingLots) {
        // Limit number of markers displayed
        if (parkingLots.size() > 50) {
            // Show only closest 50 parking lots
            Collections.sort(parkingLots, new Comparator<ParkingLot>() {
                @Override
                public int compare(ParkingLot p1, ParkingLot p2) {
                    // Sort by distance
                    return Double.compare(p1.getDistance(), p2.getDistance());
                }
            });
            
            // Keep only the first 50 (closest) parking lots
            parkingLots = parkingLots.subList(0, 50);
            Log.d(TAG, "Limited to 50 closest parking lots for performance");
        }
    }

    // Method to test data loading (Remove this test method once data loading works correctly)
    private void testParkingDataLoading() {
        List<ParkingLot> allLots = ParkingDataLoader.loadParkingLotsFromAssets(this);
        Log.d(TAG, "Total parking lots loaded: " + allLots.size());
        
        // Check for Noida parking lots
        for (ParkingLot lot : allLots) {
            if (lot.getCity().equals("Noida")) {
                Log.d(TAG, "Noida parking lot: " + lot.getName() + " at " + lot.getLatitude() + ", " + lot.getLongitude());
            }
        }
    }
} 