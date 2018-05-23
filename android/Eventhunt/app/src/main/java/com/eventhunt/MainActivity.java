package com.eventhunt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private final String TAG = MainActivity.class.getSimpleName();
    private final String[] PERMISSIONS_REQUEST = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int INIT_REQUEST_CODE = 1000;

    private Set<String> mPermissionGranted;
    private ProgressBar progressBar;
    private LocationManager mLocationManager;
    private GoogleMap mMap;

    // TODO create activity for entry in app (Login activity)
    // TODO check user after login or come up with another logic for checking users
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionGranted = new HashSet<>();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    public void checkPermission(){
        List<String> listPermission = new ArrayList<>();
        for(String permission : PERMISSIONS_REQUEST){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                listPermission.add(permission);
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    showExplanation(permission);
                }
            } else {
                addInPermissionGranted(permission);
            }
        }
        if(listPermission.size() > 0)
            ActivityCompat.requestPermissions(this, listPermission.toArray(new String[0]), INIT_REQUEST_CODE);
        else{
            progressBar.setVisibility(View.GONE);
        }
    }

    // TODO if mMap is show on activity, but we haven't yet received permission, after that we should run method for detect user location
    private void addInPermissionGranted(String permission){
        if(!mPermissionGranted.add(permission)){
            Log.w(TAG, "permission not added in Set (" + permission + ")");
        }
    }

    private void showExplanation(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                Toast.makeText(this, "explanation for " + permission, Toast.LENGTH_SHORT).show();
                break;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                Toast.makeText(this, "explanation for " + permission, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "don't know about this permission (" + permission + ")", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "don't know about this permission (" + permission + ")");
                break;
        }
    }

    public void init() {
        progressBar = findViewById(R.id.progressBar);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

/*// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };*/

// Register the listener with the Location Manager to receive location updates

//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INIT_REQUEST_CODE:
                for (String permission : permissions) {
                    addInPermissionGranted(permission);
                }
                break;
            default:
                StringBuilder builder = new StringBuilder();
                for (String s : permissions) {
                    builder.append(s).append("\n");
                }
                Log.w(TAG, "Request unknown permissions: " + builder.toString());
                break;
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_search:
                // TODO add action for search
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                break;
            case R.id.nav_adv_search:
                // TODO add action for advanced search
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                break;
            case R.id.nav_my_filters:
                // TODO add action for filters
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                break;
            case R.id.nav_add_event:
                // TODO add action for adding events
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                break;
            case R.id.nav_setting:
                // TODO add action for setting
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                break;
            default:
                // TODO add action for other cases
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng position = new LatLng(-34, 151);
        try {
            mMap.setMyLocationEnabled(true);
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            }, null);
        } catch (SecurityException e){
            Log.w(TAG, "We don't have permission for detected user location!\n" + e.getMessage());
        }
        mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

    }
}
