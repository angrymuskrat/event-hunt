package com.eventhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapModel extends ViewModel {
    private static final String TAG = MapModel.class.getSimpleName();

    private MutableLiveData<CameraPosition> cameraPositionLiveData;
    private LocationManager mLocationManager;
    private Map<String, Boolean> permissionIsCalled;


    public MapModel() {
        cameraPositionLiveData = new MutableLiveData<>();
        permissionIsCalled = new HashMap<>();
    }

    public void setPermission(String[] permissions) {
        for (String p : permissions) {
            permissionIsCalled.put(p, false);
        }
    }

    public void callAfterPermission(String permission, PermissionTask permissionTask) {
        if (permissionIsCalled.containsKey(permission) || (permissionIsCalled.get(permission) != null && !permissionIsCalled.get(permission))) {
            permissionIsCalled.put(permission, true);
            permissionTask.task();
        }
    }

    public void setLocationManager(Context context) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public void setCameraPositionLiveData(CameraPosition cameraPosition) {
        Boolean perm = permissionIsCalled.get(Manifest.permission.ACCESS_FINE_LOCATION);
        if (perm != null && perm)
            cameraPositionLiveData.setValue(cameraPosition);
    }


    public LiveData<CameraPosition> getCameraPosition() {
        if (cameraPositionLiveData.getValue() == null) {
            updateCameraPosition();
        }
        return cameraPositionLiveData;
    }

    @SuppressLint("MissingPermission")
    private void updateCameraPosition() {
        Boolean perm = permissionIsCalled.get(Manifest.permission.ACCESS_FINE_LOCATION);
        Log.w(TAG, (perm == null) + " " + perm);
        if (perm != null && perm) {
            final LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.w(TAG + "Location", "LocationChanged");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(10).build();
                    cameraPositionLiveData.setValue(cameraPosition);
                    mLocationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.w(TAG + "Location", "StatusChanged");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.w(TAG + "Location", "ProviderEnabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.w(TAG + "Location", "ProviderDisable");
                }
            };
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        }

    }

}