package com.eventhunt;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import com.eventhunt.entity.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS_REQUEST = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int INIT_REQUEST_CODE = 1000;

    private Set<String> mPermissionGranted;
    private FragmentManager mFragmentManager;
    private ProgressBar progressBar;
    private MapModel mMapModel;
    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;

    // TODO create activity for entry in app (Login activity)
    // TODO check user after login or come up with another logic for checking users
    // TODO check google account and get Token
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        mPermissionGranted = new HashSet<>();
        mMapModel = ViewModelProviders.of(this).get(MapModel.class);
        mMapModel.setLocationManager(this);
        mFragmentManager = getSupportFragmentManager();
        checkGoogleAccount();
        init();
    }

    private void checkGoogleAccount(){
        GoogleSignInAccount mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(mGoogleSignInAccount == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            if (User.isEmpty()) {
                User.setAccount(mGoogleSignInAccount);
            } else if (!User.getAccount().equals(mGoogleSignInAccount)) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            if(User.getAccount().getIdToken() == null){
                //firebaseAuthWithGoogle(User.getAccount());
                //Log.w(TAG, User.getAccount().getServerAuthCode());
            }
            updateUi();
            Log.w(TAG, mGoogleSignInAccount.getDisplayName());
        }
    }

    private void updateUi(){

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            System.out.println("signInWithCredential" + task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        } else
                            Log.w(TAG, task.getResult().getUser().getDisplayName() + " " + task.getResult().getUser().getPhoneNumber());
                    }
                });

    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Log.w(TAG, idToken);
            // TODO(developer): send ID Token to server and validate

            User.setAccount(account);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            User.setAccount(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        LiveData<CameraPosition> position = mMapModel.getCameraPosition();
        if(position != null){
            if(mMap != null)
                if(position.getValue() != null) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position.getValue()));
                }
            else
                Log.w(TAG, "mMap is null");

        } else
            Log.w(TAG, "position is null");
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
        } else{
            switch (permission){
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    mMapModel.callAfterPermission(permission, new PermissionTask() {
                        @Override
                        public void task() {
                            /*final LiveData<CameraPosition> position = mMapModel.getCameraPosition();
                            position.observeForever(new Observer<CameraPosition>() {
                                @Override
                                public void onChanged(@Nullable CameraPosition cameraPosition) {
                                    if(cameraPosition != null) {
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        position.removeObserver(this);
                                    }

                                }
                            });*/
                            mMapModel.getCameraPosition();
                        }
                    });
                    break;
            }
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapModel.setCameraPositionLiveData(mMap.getCameraPosition());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INIT_REQUEST_CODE:
                for (String permission : permissions) {
                    addInPermissionGranted(permission);
                    Log.w(TAG, "permission is granted " + permission);
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
                findViewById(R.id.fragment_layout).setVisibility(View.VISIBLE);
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, new FilterFragment()).commit();
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
            final LiveData<CameraPosition> cameraPositionLiveData = mMapModel.getCameraPosition();
            cameraPositionLiveData.observeForever(new Observer<CameraPosition>() {
                @Override
                public void onChanged(@Nullable CameraPosition cameraPosition) {
                    if(cameraPosition != null) {
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        cameraPositionLiveData.removeObserver(this);
                    }
                }
            });
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMapModel.getCameraPosition().getValue()));
        } catch (SecurityException e){
            Log.w(TAG, "We don't have permission for detected user location!\n" + e.getMessage());
        }
        mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

    }
}
