package com.eventhunt;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eventhunt.entity.Event;
import com.eventhunt.entity.User;
import com.eventhunt.model.EventModel;
import com.eventhunt.util.ExecutorUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS_REQUEST = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int INIT_REQUEST_CODE = 1000;
    private static final int FILTER_REQUEST_CODE = 1002;
    private static final int ADD_EVENT_REQUEST_CODE = 1003;

    private MapModel mMapModel;
    private EventModel mEventModel;

    private FragmentManager mFragmentManager;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private View addEventLayout;
    private Button findButton;
    private Button addEventButton;
    private Button removeEventButton;
    private NavigationView navigationView;
    private View fragmentLayout;
    private DrawerLayout drawer;
    private EditText findEditText;
    private Toolbar myToolbar;

    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private boolean isUpdated = false;
    private boolean enableMarkerFunction = false;
    private List<Marker> markerCreateList;
    private Marker selectedMarker;

    public class OnMarkerClickListenerImpl implements GoogleMap.OnMarkerClickListener{
        private final String TAG = OnMarkerClickListenerImpl.class.getSimpleName();

        @Override
        public boolean onMarkerClick(Marker marker) {
            if(marker.getTag() != null) {
                if ((Integer) marker.getTag() == 0) {
                    Log.w(TAG, marker.getTitle() + " " + marker.getPosition().toString());
                    marker.setSnippet(marker.getPosition().latitude + ";" + marker.getPosition().longitude);
                    marker.showInfoWindow();
                    selectedMarker = marker;
                    return true;
                } else if((Integer)marker.getTag() == 1){
                    selectedMarker = marker;
                    fragmentLayout.setVisibility(View.VISIBLE);
                    addEventLayout.setVisibility(View.GONE);
                    marker.showInfoWindow();
                    Event buf = mEventModel.getEventById(marker.hashCode());
                    if(buf != null)
                        mFragmentManager.beginTransaction().replace(R.id.frag_event,
                                InfoEventFragment.getInstance(buf)).commit();
                    else
                        Log.e(TAG, "{marker = " + marker.toString() + "}" );
                    return true;
                }
            }
            return false;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        mMapModel = ViewModelProviders.of(this).get(MapModel.class);
        mEventModel = ViewModelProviders.of(this).get(EventModel.class);
        mMapModel.setLocationManager(this);
        mMapModel.setPermission(PERMISSIONS_REQUEST);
        mFragmentManager = getSupportFragmentManager();
        checkGoogleAccount();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        return true;
    }



    private void checkGoogleAccount(){
        GoogleSignInAccount mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(mGoogleSignInAccount == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.w(TAG, "start LoginActivity");
            startActivity(intent);
            finish();
        } else {
            Log.w(TAG, "user is empty? " + User.isEmpty());
            if (User.isEmpty()) {
                User.setAccount(mGoogleSignInAccount);
            } else if (!User.getAccount().equals(mGoogleSignInAccount)) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            if(User.getAccount().getIdToken() == null){
                firebaseAuthWithGoogle(User.getAccount());
                Log.w(TAG, User.getAccount().getIdToken());
            } else
                Log.w(TAG, User.getAccount().getIdToken());
            isUpdated = true;
            Log.w(TAG, mGoogleSignInAccount.getDisplayName());
        }
    }

    private void updateUi(){

        //lastName.setText(User.getAccount().getFamilyName());
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
        findEditText.clearFocus();
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

    private void addInPermissionGranted(String permission) {
        switch (permission) {
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
                        Log.w(TAG, "in method task");
                        mMapModel.getCameraPosition();
                    }
                });
                break;
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
        Log.w(TAG, "init");
        Geocoder geocoder = new Geocoder(MainActivity.this);
        markerCreateList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        myToolbar = findViewById(R.id.my_toolbar);
        findEditText = myToolbar.findViewById(R.id.et_find_toolbar);
        findEditText.setFocusableInTouchMode(true);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_view);
        } else
            Log.w(TAG, "ActionBar is null!");

        findEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(actionBar == null)
                    return;
                if((actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) == ActionBar.DISPLAY_HOME_AS_UP && hasFocus)
                    actionBar.setDisplayHomeAsUpEnabled(false);
                else if(!hasFocus)
                    actionBar.setDisplayHomeAsUpEnabled(true);
                Log.w(TAG, "displayOptions = " + actionBar.getDisplayOptions()
                + " vs " + ActionBar.DISPLAY_HOME_AS_UP);

            }
        });
        findEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    findEditText.clearFocus();
                }
                return true;
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View viewMap = mapFragment.getView();
        //viewMap.setAnimation(new MapResizeAnimation(viewMap, 200));

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mFragmentManager = getSupportFragmentManager();

        fragmentLayout = findViewById(R.id.frag_event);
        addEventLayout = findViewById(R.id.add_event_layout);
        frameLayout = findViewById(R.id.frameLayout2);
        findButton = findViewById(R.id.btn_find_map);
        addEventButton = findViewById(R.id.btn_add_event_bottom);
        removeEventButton = findViewById(R.id.btn_remove_event_bottom);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddEventActivity(selectedMarker);
            }
        });

        removeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedMarker != null){
                    selectedMarker.remove();
                    selectedMarker = null;
                }
            }
        });

        if(isUpdated) {
            View view = navigationView.getHeaderView(0);
            TextView name = view.findViewById(R.id.tv_first_name);
            TextView lastName = view.findViewById(R.id.tv_second_name);
            //Log.w(TAG, User.getAccount().getDisplayName() + (name == null));
            name.setText(User.getAccount().getGivenName());
            lastName.setText(User.getAccount().getFamilyName());
            ImageView imageView = view.findViewById(R.id.iv_photo);
            Picasso.get().load(User.getAccount().getPhotoUrl()).into(imageView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "Stop?");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.toolbar_find_btn:
                if(!findEditText.hasFocus() && findEditText.getText().toString().isEmpty()) {
                    findEditText.requestFocus();
                } else {
                    Log.w(TAG + "Find", "find " + findEditText.getText());
                    Toast.makeText(this, "find " + findEditText.getText(), Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean isSelected = false;
        Intent intent;
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
                //intent = new Intent(this, FilterActivity.class);
                //startActivityForResult(intent, FILTER_REQUEST_CODE);
                break;
            case R.id.nav_add_event:
                Log.i(TAG, item.getTitle() + " item in navigation is clicked");
                //intent = new Intent(this, AddEventActivity.class);
                //startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
                enableMarkerFunction = !enableMarkerFunction;
                if(enableMarkerFunction) {
                    addMarkerFunctional();

                } else {
                    removeMarkerFunction();
                    item.setChecked(false);
                }
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
        drawer.closeDrawers();
        return true;
    }

    private void addMarkerFunctional() {
        if (mMap != null) {
            addEventLayout.setVisibility(View.VISIBLE);
            findButton.setVisibility(View.GONE);
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    marker.setSnippet(marker.getPosition().latitude + ";" + marker.getPosition().longitude);
                    Log.w(TAG, marker.getPosition().latitude + " " + marker.getPosition().longitude);
                    marker.showInfoWindow();

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if(selectedMarker != null && (int) selectedMarker.getTag() == 0) {
                        selectedMarker.remove();
                    }
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .draggable(true)
                            .title("marker");
                    Marker bufMarker = mMap.addMarker(markerOptions);
                    bufMarker.setTag(0);
                    selectedMarker = bufMarker;
                }
            });
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(final Marker marker) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Событие")
                            .setMessage("Добавить событие?")
                            .setIcon(R.drawable.ic_add_event_black)
                            .setCancelable(true)
                            .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startAddEventActivity(marker);
                                }
                            })
                            .setNegativeButton("Удалить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            marker.remove();
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

    public void startAddEventActivity(Marker marker){
        Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
        Geocoder geocoder = new Geocoder(this);
        try {
            Address address = null;
            if(marker != null)
                address = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).get(0);
            if(address != null)
                intent.putExtra("Address", address);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Не удалось получить адрес", Toast.LENGTH_SHORT).show();
        }
        startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
    }

    public void removeMarkerFunction(){
        if(mMap != null){
            if((int)selectedMarker.getTag() == 0)
                selectedMarker.remove();
            else {
                selectedMarker.setDraggable(false);
            }
            selectedMarker = null;
            addEventLayout.setVisibility(View.GONE);
            findButton.setVisibility(View.VISIBLE);
            mMap.setOnMarkerDragListener(null);
            mMap.setOnMapLongClickListener(null);
            mMap.setOnInfoWindowClickListener(null);
        }
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
        LiveData<List<Event>> events = mEventModel.getAllEvent();
        events.observeForever(new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable List<Event> events) {
                if(events != null && events.size() > 0)
                    for(Event event : events){
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(event.getPosition())
                                .title(event.getTitle())
                                .snippet(event.getType()));
                        marker.setTag(1);
                        event.setIdMarker(marker.hashCode());
                    }
            }
        });
        AddInfoWindowAdapter addInfoWindowAdapter = new AddInfoWindowAdapter(this);
        mMap.setInfoWindowAdapter(addInfoWindowAdapter);
        mMap.setOnMarkerClickListener(new OnMarkerClickListenerImpl());
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedMarker = null;
                fragmentLayout.setVisibility(View.GONE);
                if(myToolbar.hasFocus()){
                    findEditText.setFocusableInTouchMode(false);
                    findEditText.setFocusable(false);
                    findEditText.setFocusableInTouchMode(true);
                    findEditText.setFocusable(true);
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                if(enableMarkerFunction)
                    addEventLayout.setVisibility(View.VISIBLE);
            }
        });
        try {
            final LiveData<CameraPosition> cameraPositionLiveData = mMapModel.getCameraPosition();
            cameraPositionLiveData.observeForever(new Observer<CameraPosition>() {
                @Override
                public void onChanged(@Nullable CameraPosition cameraPosition) {
                    Log.w(TAG, "call?");
                    if(cameraPosition != null) {
                        Log.w(TAG, "moveCamera");
                        mMap.setMyLocationEnabled(true);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        cameraPositionLiveData.removeObserver(this);
                    }
                }
            });
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMapModel.getCameraPosition().getValue()));
        } catch (SecurityException e){
            Log.w(TAG, "We don't have permission for detected user location!\n" + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILTER_REQUEST_CODE){
            if(resultCode == FilterActivity.FILTER_RESULT_CODE){
                String event = data.getStringExtra(FilterActivity.TYPE_EVENT);
                String genre = data.getStringExtra(FilterActivity.TYPE_GENRE);
                int time = data.getIntExtra(FilterActivity.TIME, -1);
                int cost = data.getIntExtra(FilterActivity.COST, -1);
                Log.w(TAG, event + " " + genre + " " + time + " " + cost);
            }
        }
        if(requestCode == ADD_EVENT_REQUEST_CODE){
            if(resultCode == AddEventActivity.RESULT_SUCCESSFUL_CODE){
                Event event = data.getParcelableExtra(AddEventActivity.EVENT_KEY);
                Log.w(TAG, selectedMarker.getId());
                User.addEvent(selectedMarker.hashCode(), event);
                mEventModel.insertToDB(event);
                selectedMarker.setDraggable(false);
                selectedMarker.setTag(1);
                selectedMarker.showInfoWindow();
                markerCreateList.add(selectedMarker);
                Log.w(TAG + "Event", event.toString());
            }
        }
    }
}
